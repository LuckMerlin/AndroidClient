package com.merlin.transport;

import android.content.Context;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.media.NasMediaBuffer;
import com.merlin.util.Closer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;

public final class Downloader extends Transporter<Download,DownloadBody>{

    private interface Api{
        @Streaming
        @POST(Address.PREFIX_FILE+"/download")
        @FormUrlEncoded
        Call<ResponseBody> download(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek);
    }

    public Downloader(Context context){
        super(context);
    }


    @Override
    protected DownloadBody onAddTransport(Download download, TransportUpdate update, boolean interactive) {
        if (null==download){
            Debug.W(getClass(),"Can't download file which is NULL.");
            return null;
        }
        final String path=download.getFromPath();
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't download file which path invalid."+path);
            return null;
        }
        final ClientMeta meta=download.getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add download file which client url invalid."+url);
            return null;
        }
        final String folder=download.getToFolder();
        final String name=download.getName();
        if (null==folder||folder.length()<=0||null==name||name.length()<=0){
            Debug.W(getClass(),"Can't download file which folder invalid.name="+name+" folder="+folder);
            return null;
        }
        final File target=new File(folder,name);
        if (target.exists()&&target.length()>0){
            Debug.W(getClass(),"Can't download file which existed.target="+target);
            return null;
        }
        Call<ResponseBody> call=prepare(Api.class, url, Executors.newSingleThreadExecutor()).download(path,0);
        if (null!=call){
           final  DownloadBody downloadBody=new DownloadBody() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody responseBody=null!=response?response.body():null;
                if (null!=responseBody){
                    Boolean succeed=null;
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
                                }else{
                                    if (target.exists()&&target.length()>0){
                                        //Already exist
                                    }else{
                                        os=new FileOutputStream(target);
                                        succeed=false;
                                        is = null != responseBody ? responseBody.byteStream() : null;
                                        byte[] buffer = new byte[1024];
                                        int count;
                                        long downloaded=0;
                                        long lastTime=System.currentTimeMillis(),currentTime;
                                        TransportUpdate innerUpdate=update;
                                        while ((count=is.read(buffer))>0){
                                            if (isCanceled()){
                                                break;
                                            }
                                            downloaded+=count;
                                            currentTime=System.currentTimeMillis();
                                            os.write(buffer,0,count);
                                            if (null!=innerUpdate){
                                                innerUpdate.onTransportProgress(downloaded,contentLength,
                                                        currentTime>lastTime?(count/1024.f)/((currentTime-lastTime)/1000):0);
                                            }
                                            lastTime=currentTime;
                                        }
                                        succeed=contentLength==target.length();
                                        Debug.D(getClass(),(succeed?"Succeed":"Failed")+" download file "+target+" from "+path);
                                    }
                                }
                            }else{
                                String responseText=responseBody.string();
                                Debug.D(getClass(),"AAAAAAA "+responseText);
                                if (null!=update){
                                    update.onTransportFinish(false);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }finally {
                            new Closer().close(is,os);
                            if (null!=succeed&&!succeed&&null!=target){
                                Debug.D(getClass(),"Delete download fail file."+target);
                                target.delete();
                            }
                            if (null!=update){
                                update.onTransportFinish(null!=succeed&&succeed);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Debug.E(getClass(),"Exception download file ."+t+" "+folder,t);
                    if (null!=update){
                        update.onTransportFinish(false);
                    }
            }};
           Debug.D(getClass(),"Download file "+path+" to "+target);
            call.enqueue(downloadBody);
            return downloadBody;
        }
        return null;
    }
}
