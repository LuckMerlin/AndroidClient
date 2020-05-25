package com.merlin.player1;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.player.Media;
import com.merlin.player.Meta;
import com.merlin.server.Retrofit;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public class NasMedia extends Media<String> {

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
    private final String mUrl;

    public NasMedia(Retrofit retrofit,String path,String url, int bufferSize){
        super(path,bufferSize);
        mUrl=url;
        mRetrofit=retrofit;
    }

    @Override
    public final boolean open() {
        return null!=mRetrofit;
    }

    @Override
    public final boolean isOpened() {
        return null!=mRetrofit;
    }

    @Override
    public final boolean close() {
        return true;
    }

    int ssss=0;
    @Override
    protected int onReadBytes(long start, int offset, byte[] buffer,int size) {
        Retrofit retrofit=mRetrofit;
        String path=getSrc();
        String url=mUrl;
        if (null==retrofit||null==path||path.length()<=0||null==url||url.length()<=0){
            Debug.W(getClass(),"Can't read nas media which arg invalid."+path+" "+url+" "+retrofit);
            return -1;
        }
        try {
            Response<ResponseBody> response=retrofit.prepare(Api.class,url).getMediaBytes(path,ssss,size).execute();
            ResponseBody body=null!=response?response.body():null;
            MediaType mediaType=null!=body?body.contentType():null;
            String contentType=null!=mediaType?mediaType.subtype():null;
            InputStream inputStream=null!=contentType&&contentType.equals("octet-stream")?body.byteStream():null;
            if (null!=inputStream) {
                int readed= inputStream.read(buffer,offset,size);
                ssss+=readed;
                Debug.D(getClass(),"DDDDDDDDd "+ssss+" "+readed+" "+offset+" "+size);
                return readed;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public Meta getMeta() {
        Retrofit retrofit=mRetrofit;
        String md5=getSrc();
        String url=mUrl;
        if (null==retrofit||null==md5||md5.length()<=0||null==url||url.length()<=0){
            Debug.W(getClass(),"Can't get nas media meta which arg invalid."+md5+" "+url+" "+retrofit);
            return null;
        }
        try {
            Response<Reply<NasFile>> response=retrofit.prepare(Api.class,url).getMediaMeta(md5).execute();
            Reply<NasFile> reply=null!=response?response.body():null;
            NasFile nasFile= null!=reply?reply.getData():null;
            long length=-1;
            if (null!=nasFile){
                length=nasFile.getLength();
            }
            return length>=0?new Meta(length):null;
        } catch (IOException e) {
            Debug.E(getClass(),"Exception get nas media meta.e="+e+" "+md5+" "+url+" "+retrofit,e);
            e.printStackTrace();
            return null;
        }
    }
}
