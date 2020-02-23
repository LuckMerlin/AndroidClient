package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.adapter.SheetMediasAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.bean.NasMedia;
import com.merlin.bean.Sheet;
import com.merlin.media.MediaPlayService;
import com.merlin.player1.MPlayer;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class MediaSheetDetailModel extends Model implements Model.OnActivityIntentChange, Label, OnTapClick {
    private final ObservableField<Sheet> mSheet=new ObservableField<>();
    private final SheetMediasAdapter mAdapter=new SheetMediasAdapter(){
        @Override
        protected boolean onPageLoad(String title, int from, OnApiFinish<Reply<SectionData<NasMedia>>> finish) {
            return null!=call(Api.class,finish).queryMedias(title,from,from+20);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/medias")
        @FormUrlEncoded
        Observable<Reply<SectionData<NasMedia>>> queryMedias(@Field(LABEL_ID) String id, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);
    }


    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        Object object=null!=intent?getActivityDataFromIntent(intent):null;
        if (null!=object&&object instanceof Sheet){
            mSheet.set((Sheet)object);
            loadSheetMedias("While model intent changed.");
        }
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            default:
                if (null!=data&&data instanceof NasMedia){
                    MediaPlayService.play(getContext(),(NasMedia)data,0, clickCount>1?MPlayer.PLAY_TYPE_PLAY_NOW&MPlayer.PLAY_TYPE_ADD_INTO_QUEUE:MPlayer.PLAY_TYPE_PLAY_NOW);
                }
                break;
        }
        return true;
    }

    private boolean loadSheetMedias(String debug){
        ObservableField<Sheet> field=mSheet;
        SheetMediasAdapter adapter=null!=field?mAdapter:null;
        Sheet sheet=null!=field?field.get():null;
        String title=null!=sheet?sheet.getId():null;
        return null!=adapter&&adapter.loadPage(title,debug);
    }

    public ObservableField<Sheet> getSheet() {
        return mSheet;
    }

    public SheetMediasAdapter getAdapter() {
        return mAdapter;
    }
}
