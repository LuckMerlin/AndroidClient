package com.merlin.player1;

import androidx.databinding.ObservableField;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
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

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;

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
        Observable<ResponseBody> getMediaBytes(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek,
                                                    @Field(Label.LABEL_SIZE)int size);

        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Call<Reply<NasFile>> getMediaMeta(@Field(Label.LABEL_PATH) String path);
    }

    private final Retrofit mRetrofit;
    private int id;
    private String folder;
    private String name;
//    private Object imageUrl;
    private String md5;
    private long loadTime;
    private String mime;
    private String extension;
    private String url;


    public NasMedia(Retrofit retrofit,String md5,String url){
        this.md5=md5;
        this.url=url;
        mRetrofit=retrofit;
    }

    @Override
    public final boolean open() {
        Retrofit retrofit=mRetrofit;
        String md5=this.md5;
        String url=mUrl;
        if (null==retrofit||null==md5||md5.length()<=0||null==url||url.length()<=0){
            Debug.W(getClass(),"Can't get nas media meta which arg invalid."+md5+" "+url+" "+retrofit);
            return false;
        }
        try {
            Response<Reply<NasFile>> response=retrofit.prepare(Api.class,url).getMediaMeta(md5).execute();
            Reply<NasFile> reply=null!=response?response.body():null;
            NasFile nasFile= null!=reply?reply.getData():null;
            if (null!=nasFile){
                mMeta=new Meta(nasFile.getLength(),nasFile.getName(),nasFile.getTitle());
            }
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
        return null!=retrofit.prepare(Api.class,url).getMediaBytes(md5,0,0).observeOn(Schedulers.io()).
                subscribeOn(Schedulers.io()).subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(ResponseBody response) throws Exception {
                MediaType mediaType=null!=response?response.contentType():null;
                String contentType=null!=mediaType?mediaType.subtype():null;
                InputStream inputStream=null!=contentType&&contentType.equals("octet-stream")?response.byteStream():null;
                cacheReady.onCacheReady(inputStream);
            }
        });
    }

    public String getMd5() {
        return mMd5;
    }

    @Override
    public Meta getMeta() {
        return mMeta;
    }
}
