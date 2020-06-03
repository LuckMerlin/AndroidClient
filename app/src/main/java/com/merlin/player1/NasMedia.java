package com.merlin.player1;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.player.Meta;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.server.Retrofit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public class NasMedia implements Playable {

    private interface Api{
        @Streaming
        @POST(Address.PREFIX_MEDIA_PLAY+"/file")
        @FormUrlEncoded
        Call<ResponseBody> getMediaBytes(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek,
                                           @Field(Label.LABEL_SIZE)int size);

        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Call<Reply<NasFile>> getMediaMeta(@Field(Label.LABEL_PATH) String path);
    }

    private final Retrofit mRetrofit;
    private Meta mMeta;
    private final String mUrl;
    private final String mMd5;

    public NasMedia(Retrofit retrofit,String md5,String url){
        mMd5=md5;
        mUrl=url;
        mRetrofit=retrofit;
    }

    @Override
    public final boolean open() {
        Retrofit retrofit=mRetrofit;
        String md5=mMd5;
        String url=mUrl;
        if (null==retrofit||null==md5||md5.length()<=0||null==url||url.length()<=0){
            Debug.W(getClass(),"Can't get nas media meta which arg invalid."+md5+" "+url+" "+retrofit);
            return false;
        }
        try {
            Response<Reply<NasFile>> response=retrofit.prepare(Api.class,url).getMediaMeta(md5).execute();
            Reply<NasFile> reply=null!=response?response.body():null;
            NasFile nasFile= null!=reply?reply.getData():null;
            long length=-1;
            if (null!=nasFile){
                length=nasFile.getLength();
            }
            mMeta=new Meta(length);
            return true;
        } catch (IOException e) {
            Debug.E(getClass(),"Exception get nas media meta.e="+e+" "+md5+" "+url+" "+retrofit,e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public final boolean isOpened() {
        return null!=mMeta;
    }

    @Override
    public final boolean close() {
        Debug.D(getClass(),"Close nas media.");
        return true;
    }

    @Override
    public boolean cache(CacheReady cacheReady) {
        Retrofit retrofit=mRetrofit;
        String md5=mMd5;
        String url=mUrl;
        if (null==retrofit||null==md5||md5.length()<=0||null==url||url.length()<=0){
            Debug.W(getClass(),"Can't read nas media which arg invalid."+md5+" "+url+" "+retrofit);
            return false;
        }
        retrofit.prepare(Api.class,url).getMediaBytes(md5,0,0).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=null!=response?response.body():null;
                MediaType mediaType=null!=body?body.contentType():null;
                String contentType=null!=mediaType?mediaType.subtype():null;
                InputStream inputStream=null!=contentType&&contentType.equals("octet-stream")?body.byteStream():null;
                try {
                    cacheReady.onCacheReady(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                try {
                    cacheReady.onCacheReady(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    @Override
    public Meta getMeta() {
        return mMeta;
    }
}
