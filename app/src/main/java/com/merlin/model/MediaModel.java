package com.merlin.model;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.PlayerBinder;
import com.merlin.player.Media;
import com.merlin.player1.NasMedia;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaModel extends Model {

    private interface FavoriteApi  {
        @POST(Address.PREFIX_FILE+"/favorite")
        @FormUrlEncoded
        Observable<Reply> makeFavorite(@Field(Label.LABEL_MD5) String md5, @Field(Label.LABEL_DATA) boolean favorite );
    }

    protected final boolean makeFavorite(Media playable, boolean favorite, OnApiFinish<Reply> finish){
        if (null==playable||!(playable instanceof NasMedia)){
            toast(R.string.cloudFileValid);
            return false;
        }
        NasMedia media=(NasMedia)playable;
        String md5=null!=media?media.getMd5():null;
        if (null==md5||md5.length()<=0){
            Debug.D(getClass(),"Can't make file favorite while md5 invalid");
            return toast(R.string.requestFail)&&false;
        }
        Debug.D(getClass(),"favorite "+favorite);
        return null!=call(prepare(FavoriteApi.class,null).makeFavorite(md5,favorite),finish);
    }

    public final ActivityMediaPlayModel getModel(){
        return ActivityMediaPlayModel.getModelFromChild(this);
    }


    public final PlayerBinder getPlayer(){
        ActivityMediaPlayModel model=getModel();
        return null!=model?model.getPlayerBinder():null;
    }


}
