package com.merlin.transport;

import android.content.Context;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.media.NasMediaBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;

public final class Downloader extends Transporter<Download,Boolean>{

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
    protected Boolean onAddTransport(Download download, TransportUpdate update, boolean interactive) {
        if (null==download){
            Debug.W(getClass(),"Can't download file which is NULL.");
            return null;
        }
        final String path=null!=download?download.getFromPath():null;
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't download file which path invalid."+path);
            return null;
        }
        final String folder=null!=download?download.getToFolder():null;
        if (null==folder||folder.length()<=0){
            Debug.W(getClass(),"Can't download file which folder invalid."+folder);
            return null;
        }
        final ClientMeta meta=download.getClient();
        final String url=null!=meta?meta.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.W(getClass(),"Can't add download file which client url invalid."+url);
            return null;
        }
        Call<ResponseBody> responseBody=prepare(Api.class,url).download(path,0);
        if (null!=responseBody){
            responseBody.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Debug.D(getClass(),"$$$$$$ "+Thread.currentThread().getName());
                ResponseBody responseBody=null!=response?response.body():null;
                if (null!=responseBody){
                    long length=responseBody.contentLength();
                    MediaType mediaType=null!=responseBody?responseBody.contentType():null;
                    String contentType=null!=mediaType?mediaType.subtype():null;
                    if (contentType!=null){
                        try {
                            if (contentType.equals("octet-stream")) {
                                InputStream is = null != responseBody ? responseBody.byteStream() : null;

                            }else{
                                String responseText=responseBody.string();
                                Debug.D(getClass(),"AAAAAAA "+responseText);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
            }
            });
            return true;
        }
        return null;
    }
}
