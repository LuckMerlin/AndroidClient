package com.merlin.media;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.bean.NasMedia;
import com.merlin.retrofit.Retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public final class NasMediaBuffer extends NetMediaBuffer<NasMedia> {

    private interface Api {
        @Streaming
        @POST(Address.PREFIX_MEDIA_PLAY+"/file")
        @FormUrlEncoded
        Call<ResponseBody> playMediaFile(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_MD5)
                String md5,@Field(Label.LABEL_POSITION) double seek);
    }

    public NasMediaBuffer(NasMedia media, double seek){
        super(media,seek);
    }

    @Override
    protected  Call<ResponseBody> onResolvePlayCall(NasMedia media) {
        String md5=null!=media?media.getMd5():null;
        if (null!=md5&&md5.length()>0){
            double seek=getSeek();
//            seek=0.5f;
            return new Retrofit().call(Api.class,null,null).playMediaFile(null,md5,seek);
        }
        return null;
    }
}
