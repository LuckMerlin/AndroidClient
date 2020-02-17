package com.merlin.model;

import android.app.Activity;
import android.content.Intent;

import androidx.databinding.ObservableField;

import com.merlin.adapter.SheetMediasAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.File;
import com.merlin.bean.Media;
import com.merlin.bean.Sheet;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class MediaSheetDetailModel extends Model implements Model.OnActivityIntentChange, Label {
    private final ObservableField<Sheet> mSheet=new ObservableField<>();
    private final SheetMediasAdapter mAdapter=new SheetMediasAdapter(){
        @Override
        protected boolean onPageLoad(String title, int page, OnApiFinish<Reply<PageData<Media>>> finish) {
            return null!=call(Api.class,finish).queryMedias(title,page,10);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/medias")
        @FormUrlEncoded
        Observable<Reply<PageData<Media>>> queryMedias(@Field(LABEL_ID) String id, @Field(LABEL_PAGE) int page, @Field(LABEL_LIMIT) int limit);
    }


    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        Object object=null!=intent?getActivityDataFromIntent(intent):null;
        if (null!=object&&object instanceof Sheet){
            mSheet.set((Sheet)object);
            loadSheetMedias("While model intent changed.");
        }
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
