package com.merlin.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.activity.OnServiceBindChange;
import com.merlin.adapter.SheetMediasAdapter;
import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.client.databinding.SheetMediasMenusBinding;
import com.merlin.media.ClickPlayMedia;
import com.merlin.media.PlayerBinder;
import com.merlin.player.Media;
import com.merlin.player1.NasMedia;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaSheetDetailModel extends Model implements Model.OnActivityIntentChange, OnServiceBindChange,Label, OnTapClick {
    private final ObservableField<Sheet> mSheet=new ObservableField<>();
    private final SheetMediasAdapter mAdapter=new SheetMediasAdapter(){
        @Override
        protected Canceler onPageLoad(Long title, int from, OnApiFinish<Reply<PageData<Media>>> finish) {
            return call(prepare(Api.class,null).queryMedias(title,from,from+20),finish);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/medias")
        @FormUrlEncoded
        Observable<Reply<PageData<NasMedia>>> queryMedias(@Field(LABEL_ID) long id, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);
    }
    private PlayerBinder mPlayer;

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
            case R.drawable.selector_menu:
                return showDetailContextMenu(view);
            case R.string.playAll:
                return playAll("After play all tap click.");
            default:
                if (null!=data&&data instanceof NasMedia){
                    return new ClickPlayMedia().playFromClick(mPlayer,(NasMedia)data,clickCount,"After media item tap.");
               }
                break;
        }
        return true;
    }

    @Override
    public void onServiceBindChanged(ComponentName name, IBinder service) {
        mPlayer=null!=service&&service instanceof PlayerBinder?(PlayerBinder)service:null;
    }

    private boolean playAll(String debug){
        SheetMediasAdapter adapter=mAdapter;
        ArrayList<Media> list=null!=adapter?adapter.getData():null;
        if (null==list||list.size()<=0){
            return toast(R.string.listEmpty);
        }
//        return MediaPlayService.play(getContext(),list,0,MPlayer.PLAY_TYPE_PLAY_NOW|MPlayer.PLAY_TYPE_ADD_INTO_QUEUE|MPlayer.PLAY_TYPE_CLEAN_QUEUE);
        return false;
    }

    private boolean showDetailContextMenu(View view) {
        SheetMediasMenusBinding binding=null!=view?inflate(R.layout.sheet_medias_menus):null;
        return null!=binding&&showAtLocationAsContext(view,binding);
    }

    private boolean loadSheetMedias(String debug){
        ObservableField<Sheet> field=mSheet;
        SheetMediasAdapter adapter=null!=field?mAdapter:null;
        Sheet sheet=null!=field?field.get():null;
        return null!=sheet&&null!=adapter&&adapter.loadPage(sheet.getId(),debug);
    }

    public ObservableField<Sheet> getSheet() {
        return mSheet;
    }

    public SheetMediasAdapter getAdapter() {
        return mAdapter;
    }
}
