package com.merlin.player1;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.player.Media;
import com.merlin.player.Meta;
import com.merlin.player.Player;
import com.merlin.player.SyncLoader;
import com.merlin.server.Retrofit;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public final class NasMedia extends Path implements Media {
    private interface Api{
        @Streaming
        @POST(Address.PREFIX_MEDIA_PLAY+"/file")
        @FormUrlEncoded
        Observable<ResponseBody> getMediaBytes(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_MD5) String md5,@Field(Label.LABEL_POSITION) double seek,
                                                    @Field(Label.LABEL_SIZE)int size);
        @POST(Address.PREFIX_MEDIA_PLAY+"/detail")
        @FormUrlEncoded
        Call<Reply<NasMedia>>
        getMediaMeta(@Field(Label.LABEL_MD5) String md5, @Field(Label.LABEL_PATH) String path);

    }
    private String artist;
    private String album;
    private long duration;

    private Meta mMeta;

    @Override
    public final boolean open(Player player) {
        SyncLoader loader=null!=player?player.getLoader():null;
        Retrofit retrofit=null!=loader&&loader instanceof Retrofit?(Retrofit)loader:null;
        if (null==retrofit){
            Debug.W(getClass(),"Can't get nas media meta which retrofit invalid.");
            return false;
        }
        mMeta=new NasMediaMeta(this);
//        String md5=getMd5();
//        String host=getHost();
//        String path=getPath();
//        if ((null==md5||md5.length()<=0)&&(null==path||path.length()<=0)){
//            Debug.W(getClass(),"Can't get nas media meta which md5 and path invalid."+md5+" "+path);
//            return false;
//        }
//        try {
//            Response<Reply<NasMedia>> response=retrofit.prepare(Api.class,host).getMediaMeta(md5,path).execute();
//            Reply<NasMedia> reply=null!=response?response.body():null;
//            NasMedia nasFile= null!=reply?reply.getData():null;
//            return null!=(null!=nasFile?mMeta=new NasMeta(nasFile):null);
//        } catch (Exception e) {
//            Debug.E(getClass(),"Exception get nas media meta.e="+e+" "+md5+" "+host+" "+retrofit,e);
//            e.printStackTrace();
//            return false;
//        }
        return true;
    }

    @Override
    public final boolean isOpened() {
        return null!=mMeta;
    }

    @Override
    public boolean close(Player player) {
        Debug.D(getClass(),"Close nas media.");
        mMeta=null;
        return false;
    }

    @Override
    public boolean cache(Player player,CacheReady cacheReady) {
        SyncLoader loader=null!=player?player.getLoader():null;
        Retrofit retrofit=null!=loader&&loader instanceof Retrofit?(Retrofit)loader:null;
        String md5=getMd5();
        String host=getHost();
        if (null==retrofit||((null==md5||md5.length()<=0))){
            Debug.W(getClass(),"Can't read nas media which arg invalid."+md5+" "+host+" "+retrofit);
            return false;
        }
        return null!=retrofit.prepare(Api.class,host).getMediaBytes(null,md5,0,0).observeOn(Schedulers.io()).
                subscribeOn(Schedulers.io()).subscribe((ResponseBody response)-> {
                MediaType mediaType=null!=response?response.contentType():null;
                String contentType=null!=mediaType?mediaType.subtype():null;
                InputStream inputStream=null!=contentType&&contentType.equals("octet-stream")?response.byteStream():null;
                cacheReady.onCacheReady(inputStream);
        });
    }

    @Override
    public Meta getMeta() {
        return mMeta;
    }

    public final long getDuration() {
        return duration;
    }

    public final String getAlbum() {
        return album;
    }

    public final String getArtist() {
        return artist;
    }

    private final static class NasMediaMeta extends Meta{
        private final NasMedia mMedia;

        private NasMediaMeta(NasMedia nasFile){
            mMedia=nasFile;
        }

        @Override
        public long getLength() {
            NasMedia media=mMedia;
            return null!=media?media.getLength():-1;
        }

        @Override
        public String getName() {
            NasMedia media=mMedia;
            return null!=media?media.getName():null;
        }

        @Override
        public String getTitle() {
            String title=super.getTitle();
            return null!=title&&title.length()>0?title:super.getName();
        }
    }

    public static NasMedia requestByMd5(Retrofit retrofit,String md5){
        try {
            Response<Reply<NasMedia>> response=null!=retrofit&&null!=md5?retrofit.prepare(Api.class,
                    null).getMediaMeta(md5,null).execute():null;
            Reply<NasMedia> reply=null!=response?response.body():null;
            return null!=reply?reply.getData():null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

