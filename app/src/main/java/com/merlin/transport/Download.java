package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.file.CoverMode;
import com.merlin.server.Retrofit;
import com.merlin.util.Closer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public final class Download extends AbsTransport<DownloadBody> {

    private interface Api{
        @Streaming
        @POST(Address.PREFIX_FILE+"/download")
        @FormUrlEncoded
        Call<ResponseBody> download(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek);
    }

    public Download(String fromPath,String toFolder,String name, ClientMeta client, Integer coverMode){
        super(fromPath,toFolder,name,client,coverMode);
    }

    @Override
    protected DownloadBody onStart(OnTransportUpdate update, Retrofit retrofit) {
         if (null==retrofit){
            Debug.W(getClass(),"Can't download file which retrofit is NULL.");
            notifyFinish(TRANSPORT_ERROR,"File is NULL .",null,null,null,update);
            return null;
        }
        final String path=getFromPath();
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't download file which path invalid."+path);
            notifyFinish(TRANSPORT_ERROR,"Path is invalid.",null,null,null,update);
            return null;
        }
        final ClientMeta meta=getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add download file which client url invalid."+url);
            notifyFinish(TRANSPORT_ERROR,"Client url is invalid.",null,null,null,update);
            return null;
        }
        final String folder=getToFolder();
        final String name=getName();
        if (null==folder||folder.length()<=0||null==name||name.length()<=0){
            Debug.W(getClass(),"Can't download file which folder invalid.name="+name+" folder="+folder);
            notifyFinish(TRANSPORT_ERROR,"Folder is  invalid.",null,null,null,update);
            return null;
        }
        final File target=new File(folder,name);
        if (target.exists()&&target.length()>0){
            Debug.W(getClass(),"Can't download file which existed.target="+target);
            notifyFinish(TRANSPORT_TARGET_EXIST," File existed.",null,null,null,update);
            return null;
        }
        Call<ResponseBody> call=retrofit.prepare(Api.class, url, Executors.newSingleThreadExecutor()).download(path,0);
        if (null!=call){
            final  DownloadBody downloadBody=new DownloadBody() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String note=null;Integer what=null;
                    ResponseBody responseBody=null!=response?response.body():null;
                    if (null!=responseBody){
                        MediaType mediaType=null!=responseBody?responseBody.contentType():null;
                        String contentType=null!=mediaType?mediaType.subtype():null;
                        if (contentType!=null){
                            InputStream is=null;
                            FileOutputStream os=null;
                            try {
                                if (contentType.equals("octet-stream")) {
                                    long contentLength=responseBody.contentLength();
                                    Debug.D(getClass(),"Fetched download file length "+contentLength);
                                    if (contentLength <= 0){
                                        //Invalid length
                                    }else {
                                        final int coverMode = getCoverMode();
                                        boolean needDownload = true;
                                        if (target.exists() && target.length() > 0) {
                                            if ((coverMode & CoverMode.COVER_MODE_REPLACE) > 0) {
                                                needDownload=true;
                                            } else if ((coverMode & CoverMode.COVER_MODE_SKIP) > 0) {
                                                needDownload=false;what=TRANSPORT_SKIP;note="Skip download already exist file.";
                                            }else if ((coverMode & CoverMode.COVER_MODE_KEEP_BOTH) > 0){
                                                //Nothing
                                            }
                                        }
                                        if (needDownload) {
                                            os = new FileOutputStream(target);
                                            is = null != responseBody ? responseBody.byteStream() : null;
                                            byte[] buffer = new byte[1024];
                                            int count;
                                            long downloaded = 0;
                                            long lastTime = System.currentTimeMillis(), currentTime;
                                            OnTransportUpdate innerUpdate = update;
//                                            float speed=0;
//                                            long duration=0;
                                            while ((count = is.read(buffer)) > 0) {
                                                if (isCancel()) {
                                                    what = TRANSPORT_CANCEL;note = "Download file cancel.";
                                                    break;
                                                }
                                                downloaded += count;
                                                currentTime = System.currentTimeMillis();
                                                os.write(buffer, 0, count);
                                                if (null != innerUpdate) {
//                                                    speed=count / 1024.f;
//                                                    duration=(currentTime - lastTime)/1000;
//                                                    speed=(currentTime >= lastTime ? speed / (duration==0?1:duration) : 0);
//                                                    Debug.D(getClass()," "+(count / 1024.f)+" "+speed);
//                                                    lastTime = System.currentTimeMillis();
                                                    innerUpdate.onTransportUpdate(false,TRANSPORT_PROGRESS, null, downloaded, contentLength,-1f);
                                                }
                                            }
                                            what = contentLength == target.length()?TRANSPORT_SUCCEED:TRANSPORT_FAIL;
                                            target.delete();
                                            Debug.D(getClass(), (what==TRANSPORT_SUCCEED ? "Succeed" : "Failed") + " download file " + target + " from " + path);
                                        }
                                    }
                                }else{
                                    String responseText=responseBody.string();
                                    Debug.D(getClass(),"AAAAAAA "+responseText);
                                    what=TRANSPORT_ERROR;note=responseText;
                                }
                            } catch (IOException e) {
                                what=TRANSPORT_FAIL;note="Exception download "+e+" "+target;
                                Debug.E(getClass(),note,e);
                                e.printStackTrace();
                            }finally {
                                new Closer().close(is,os);
                                if ((null==what||what!=TRANSPORT_SUCCEED)&&null!=target){
                                    Debug.D(getClass(),"Delete download fail file."+target);
                                    target.delete();
                                }
                            }
                        }
                    }
                    notifyFinish(what,note,null,null,null,update);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Debug.E(getClass(),"Exception download file ."+t+" "+folder,t);
                    notifyFinish(TRANSPORT_FAIL,"Exception download file ."+t,null,null,null,update);
                }};
            Debug.D(getClass(),"Download file "+path+" to "+target);
            call.enqueue(downloadBody);
            return downloadBody;
        }
        return null;
    }

    final void notifyFinish(Integer what,String note,Long uploaded, Long total,Float speed,OnTransportUpdate update){
        if (null!=update&&null!=what){
            update.onTransportUpdate(true,what,note,uploaded,total,speed);
        }
    }

}
