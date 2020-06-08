package com.merlin.player1;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasMediaFile;
import com.merlin.debug.Debug;
import com.merlin.player.Meta;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.player.SyncLoader;
import com.merlin.server.Retrofit;

import java.io.InputStream;

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

public final class NasMedia implements Playable {

    private interface Api{
        @Streaming
        @POST(Address.PREFIX_MEDIA_PLAY+"/file")
        @FormUrlEncoded
        Observable<ResponseBody> getMediaBytes(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_MD5) String md5,@Field(Label.LABEL_POSITION) double seek,
                                                    @Field(Label.LABEL_SIZE)int size);

        @POST(Address.PREFIX_MEDIA_PLAY+"/detail")
        @FormUrlEncoded
        Call<Reply<NasMediaFile>>
        getMediaMeta(@Field(Label.LABEL_MD5) String md5,@Field(Label.LABEL_PATH) String path);
    }

    private Meta mMeta;
    private final String mHost;
    private final String mMd5;
    private final String mPath;

    public NasMedia(NasMediaFile nasFile){
        this(null!=nasFile?nasFile.getHost():null,null!=nasFile?nasFile.getMd5():null,null!=nasFile?nasFile.getPath():null);
    }

    public NasMedia(String host,String md5,String path){
        mHost=host;
        mMd5=md5;
        mPath=path;
    }

    @Override
    public final boolean open(Player player) {
        SyncLoader loader=null!=player?player.getLoader():null;
        Retrofit retrofit=null!=loader&&loader instanceof Retrofit?(Retrofit)loader:null;
        if (null==retrofit){
            Debug.W(getClass(),"Can't get nas media meta which retrofit invalid.");
            return false;
        }
        String md5=mMd5;
        String host=mHost;
        String path=mPath;
        if ((null==md5||md5.length()<=0)&&(null==path||path.length()<=0)){
            Debug.W(getClass(),"Can't get nas media meta which md5 and path invalid."+md5+" "+path);
            return false;
        }
        try {
            Response<Reply<NasMediaFile>> response=retrofit.prepare(Api.class,host).getMediaMeta(md5,path).execute();
            Reply<NasMediaFile> reply=null!=response?response.body():null;
            NasMediaFile nasFile= null!=reply?reply.getData():null;
            return null!=(null!=nasFile?mMeta=new NasMeta(nasFile):null);
        } catch (Exception e) {
            Debug.E(getClass(),"Exception get nas media meta.e="+e+" "+md5+" "+host+" "+retrofit,e);
            e.printStackTrace();
            return false;
        }
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
        Meta meta=mMeta;
        String md5=mMd5;
        md5=null!=md5&&md5.length()>0?md5:(null!=meta?meta.getMd5():null);
        String host=mHost;
        host=null!=host&&host.length()>0?host:(null!=meta?meta.getHost():null);
        String path=mPath;
        path=null!=path&&path.length()>0?path:path;
        if (null==retrofit||((null==md5||md5.length()<=0)&&(null==path||path.length()<=0))){
            Debug.W(getClass(),"Can't read nas media which arg invalid."+md5+" "+host+" "+retrofit);
            return false;
        }
        return null!=retrofit.prepare(Api.class,host).getMediaBytes(path,md5,0,0).observeOn(Schedulers.io()).
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

    private final static class NasMeta extends Meta{
        private final NasMediaFile mNasFile;


        private NasMeta(NasMediaFile nasFile){
            super(nasFile.getLength(),nasFile.getName(),nasFile.getTitle(),nasFile.getMd5());
            Debug.D(getClass(),"AAAAAbbbbbbbb AAA  "+nasFile.getMd5());
            mNasFile=nasFile;
        }

        @Override
        public long getDuration() {
            NasMediaFile nasFile=mNasFile;
            return null!=nasFile?nasFile.getDuration():super.getDuration();
        }

        @Override
        public String getTitle() {
            String title=super.getTitle();
            return null!=title&&title.length()>0?title:super.getName();
        }

        @Override
        public String getAlbum() {
            return super.getAlbum();
        }

        @Override
        public String getArtist() {
            return super.getArtist();
        }

        @Override
        public String getHost() {
            NasMediaFile nasFile=mNasFile;
            return null!=nasFile?nasFile.getHost():super.getHost();
        }
    }
}

