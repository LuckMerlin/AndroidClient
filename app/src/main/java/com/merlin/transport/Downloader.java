//package com.merlin.transport;
//
//import android.content.Context;
//
//import com.merlin.api.Address;
//import com.merlin.api.Label;
//import com.merlin.bean.ClientMeta;
//import com.merlin.debug.Debug;
//import com.merlin.file.CoverMode;
//import com.merlin.util.Closer;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.Executors;
//
//import okhttp3.MediaType;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Response;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.POST;
//import retrofit2.http.Streaming;
//
//public final class Downloader extends AbsTransporter<Download,DownloadBody> {
//
//    private interface Api{
//        @Streaming
//        @POST(Address.PREFIX_FILE+"/download")
//        @FormUrlEncoded
//        Call<ResponseBody> download(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek);
//    }
//
//    public Downloader(Context context){
//        super(context);
//    }
//
//    @Override
//    protected DownloadBody onAddTransport(Download download, TransportUpdate update, boolean interactive) {
//        if (null==download){
//            Debug.W(getClass(),"Can't download file which is NULL.");
//            notifyFinish(false,TRANSPORT_ERROR,"File is NULL .",null,null,null,update);
//            return null;
//        }
//        final String path=download.getFromPath();
//        if (null==path||path.length()<=0){
//            Debug.W(getClass(),"Can't download file which path invalid."+path);
//            notifyFinish(false,TRANSPORT_ERROR,"Path is invalid.",null,null,null,update);
//            return null;
//        }
//        final ClientMeta meta=download.getClient();
//        final String url=null!=meta?meta.getUrl():null;
//        if (null==url||url.length()<=0){
//            Debug.W(getClass(),"Can't add download file which client url invalid."+url);
//            notifyFinish(false,TRANSPORT_ERROR,"Client url is invalid.",null,null,null,update);
//            return null;
//        }
//        final String folder=download.getToFolder();
//        final String name=download.getName();
//        if (null==folder||folder.length()<=0||null==name||name.length()<=0){
//            Debug.W(getClass(),"Can't download file which folder invalid.name="+name+" folder="+folder);
//            notifyFinish(false,TRANSPORT_ERROR,"Folder is  invalid.",null,null,null,update);
//            return null;
//        }
//        final File target=new File(folder,name);
//        if (target.exists()&&target.length()>0){
//            Debug.W(getClass(),"Can't download file which existed.target="+target);
//            notifyFinish(false,TRANSPORT_TARGET_EXIST," File existed.",null,null,null,update);
//            return null;
//        }
//        Call<ResponseBody> call=prepare(Api.class, url, Executors.newSingleThreadExecutor()).download(path,0);
//        if (null!=call){
//           final  DownloadBody downloadBody=new DownloadBody() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                String note=null;Boolean succeed=null;Integer what=null;
//                ResponseBody responseBody=null!=response?response.body():null;
//                if (null!=responseBody){
//                    MediaType mediaType=null!=responseBody?responseBody.contentType():null;
//                    String contentType=null!=mediaType?mediaType.subtype():null;
//                    if (contentType!=null){
//                        InputStream is=null;
//                        FileOutputStream os=null;
//                        try {
//                            if (contentType.equals("octet-stream")) {
//                                long contentLength=responseBody.contentLength();
//                                Debug.D(getClass(),"Fetched download file length "+contentLength);
//                                if (contentLength <= 0){
//                                    //Invalid length
//                                }else {
//                                    final int coverMode = download.getCoverMode();
//                                    boolean needDownload = true;
//                                    if (target.exists() && target.length() > 0) {
//                                        if ((coverMode & CoverMode.COVER_MODE_REPLACE) > 0) {
//                                            needDownload=true;
//                                        } else if ((coverMode & CoverMode.COVER_MODE_SKIP) > 0) {
//                                            needDownload=false;succeed=false;what=TRANSPORT_SKIP;note="Skip download already exist file.";
//                                        }else if ((coverMode & CoverMode.COVER_MODE_KEEP_BOTH) > 0){
//                                            //Nothing
//                                        }
//                                    }
//                                    if (needDownload) {
//                                        os = new FileOutputStream(target);
//                                        succeed = false;
//                                        is = null != responseBody ? responseBody.byteStream() : null;
//                                        byte[] buffer = new byte[1024];
//                                        int count;
//                                        long downloaded = 0;
//                                        long lastTime = System.currentTimeMillis(), currentTime;
//                                        TransportUpdate innerUpdate = update;
//                                        while ((count = is.read(buffer)) > 0) {
//                                            if (isCanceled()) {
//                                                succeed = false;what = TRANSPORT_CANCEL;note = "Download file cancel.";
//                                                break;
//                                            }
//                                            downloaded += count;
//                                            currentTime = System.currentTimeMillis();
//                                            os.write(buffer, 0, count);
//                                            if (null != innerUpdate) {
//                                                innerUpdate.onTransportUpdate(false,TRANSPORT_PROGRESS, null, downloaded, contentLength,
//                                                        (currentTime > lastTime ? (count / 1024.f) / ((currentTime - lastTime) / 1000) : 0));
//                                            }
//                                            lastTime = currentTime;
//                                        }
//                                        succeed = contentLength == target.length();
//                                        Debug.D(getClass(), (succeed ? "Succeed" : "Failed") + " download file " + target + " from " + path);
//                                    }
//                                }
//                            }else{
//                                String responseText=responseBody.string();
//                                Debug.D(getClass(),"AAAAAAA "+responseText);
//                                succeed=false;what=TRANSPORT_ERROR;note=responseText;
//                            }
//                        } catch (IOException e) {
//                            succeed = false;what=TRANSPORT_FAIL;note="Exception download "+e+" "+target;
//                            Debug.E(getClass(),note,e);
//                            e.printStackTrace();
//                        }finally {
//                            new Closer().close(is,os);
//                            if (null!=succeed&&!succeed&&null!=target){
//                                Debug.D(getClass(),"Delete download fail file."+target);
//                                target.delete();
//                            }
//                        }
//                    }
//                }
//                notifyFinish(succeed,what,note,null,null,null,update);
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Debug.E(getClass(),"Exception download file ."+t+" "+folder,t);
//                notifyFinish(false,TRANSPORT_FAIL,"Exception download file ."+t,null,null,null,update);
//            }};
//            Debug.D(getClass(),"Download file "+path+" to "+target);
//            call.enqueue(downloadBody);
//            return downloadBody;
//        }
//        return null;
//    }
//
//    final void notifyFinish(boolean succeed,Integer what,String note,Long uploaded, Long total,Float speed,TransportUpdate update){
//        if (null!=update&&null!=what){
//            update.onTransportUpdate(true,what,note,uploaded,total,speed);
//        }
//    }
//}
