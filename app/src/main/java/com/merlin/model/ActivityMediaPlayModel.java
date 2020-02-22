package com.merlin.model;


import android.view.View;
import android.view.ViewParent;

import androidx.databinding.ObservableField;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaAdapter;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.File;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasMedia;
import com.merlin.bean.Sheet;
import com.merlin.binding.StatusBar;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.MediaPlayService;
import com.merlin.media.MediaPlayer;
import com.merlin.media.Mode;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.task.Transporter;
import com.merlin.transport.TransportService;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ActivityMediaPlayModel extends Model implements OnTapClick, What, Label,OnPlayerBindChange,OnPlayerStatusUpdate {
    private MediaPlayer mPlayer;
    private final ObservableField<Integer> mStatus=new ObservableField<>();
    private final ObservableField<NasMedia> mPlaying=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final ObservableField<Integer> mCurrPosition=new ObservableField<>();

    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter();

    private interface Api{
        @POST(Address.PREFIX_FILE+"/favorite")
        @FormUrlEncoded
        Observable<Reply<NasFile>> makeFavorite(@Field(LABEL_MD5) String md5, @Field(LABEL_DATA) boolean favorite );
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                if (null!=data&&data instanceof Sheet){
                    showMediaSheetDetail((Sheet)data);
                }else{
                    switch (resId){
                        case R.drawable.selector_media_pause://Get through
                        case R.drawable.selector_media_play:
                            return pause_play("After play_pause tap click.");
                        case R.drawable.single_normal://Get through
                            return changePlayMode(resId,"After mode tap click.");
                        case R.drawable.selector_pre:
                            return pre("After pre media tap click.");
                        case R.drawable.selector_menu:
                            return showPlayingQueue("After menu tap click.");
                        case R.drawable.selector_next:
                            return next("After next media tap click.");
                        case R.drawable.selector_download_media:
                            NasMedia media=mPlaying.get();
                            return null!=media&&TransportService.download(getContext(),media,"After play display download tap.");
                        case R.drawable.heart_pressed://Get through
                        case R.drawable.heart_normal://get through
                        case R.drawable.selector_heart:
                            if (null!=view&&null!=data){
                                makeFavorite(data,!view.isSelected());
                            }
                            return true;


                            //                        default:
//                            if (null!=data&&data instanceof File){
//                                MediaPlayService.play(getContext(),data,0,false);
//                            }
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onPlayerBindChanged(MediaPlayer player) {
        MediaPlayer curr=mPlayer;
        if (null!=curr){
            mPlayer=null;
            curr.removeListener(this);
        }
        if (null!=player){
            mPlayer=player;
            player.addListener(this);
            updatePlaying("After player bind.");
        }
    }

    @Override
    public void onPlayerStatusUpdated(Player player, int status, String note, Playable media, Object data) {
        mStatus.set(status);
        switch (status){
            case STATUS_START:
                updatePlaying("While status start.");
                break;
            case STATUS_PROGRESS:
                Debug.D(getClass(),"^^^^^^^^ "+data);
                break;
        }
    }

    private boolean changePlayMode(Integer id,String debug){
        MediaPlayer player=mPlayer;
        if (null!=player){
//            return player.playMode(Mode.SINGLE);
        }
        return false;
    }

    private boolean pre(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&player.pre(debug);
    }

    private boolean next(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&player.next(debug);
    }

    private boolean showPlayingQueue(String debug){

        return false;
    }

    private boolean makeFavorite(Object media,boolean favorite){
        String md5=null!=media?media instanceof NasMedia?((NasMedia)media).getMd5():null:null;
        toast(""+media);
        if (null==md5||md5.length()<=0){
            return false;
        }
        Debug.D(getClass(),"favorite "+favorite);
        return null!=call(Api.class,(OnApiFinish<Reply<NasFile>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED&&null!=data){
//                ((NasMedia)m)
//                  if ()
//                adapter.notifyFavoriteChange(md5, favorite);
            }
        }).makeFavorite(md5,favorite);
    }

    private boolean pause_play(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&player.togglePlayPause(null);
    }

    private void updatePlaying(String debug){
        final MediaPlayer player=mPlayer;
        Playable playable=null!=player?player.getPlaying():null;
        NasMedia playing=null!=playable&&playable instanceof NasMedia ?(NasMedia)playable:null;
        String title=null!=playing?playing.getTitle():null;
        mPlaying.set(playing);
        setStatusBar(null!=playing?title:R.string.mediaPlay, StatusBar.CENTER);
    }

    private boolean showMediaSheetDetail(Sheet sheet){
        if (null!=sheet){
            return startActivity(MediaSheetDetailActivity.class,sheet);
        }
        Debug.W(getClass(),"Can't show media sheet detail.sheet="+sheet);
        return false;
    }

    public ObservableField<Integer> getStatus() {
        return mStatus;
    }

    public MediaPlayDisplayAdapter getDisplayAdapter() {
        return mDisplayAdapter;
    }

    public ObservableField<NasMedia> getPlaying() {
        return mPlaying;
    }

    public ObservableField<Integer> getProgress() {
        return mProgress;

    }

    public ObservableField<Integer> getCurerntPosition() {
        return mCurrPosition;
    }

    public static ActivityMediaPlayModel getModelFromChild(Model model){
        View view=null!=model?model.getRoot():null;
        ViewParent parent=null!=view?view.getParent():null;
        parent=null!=parent?parent.getParent():null;
        Object object=null!=parent&&parent instanceof View?((View)parent).getTag(R.id.modelBind):null;
        return null!=object&&object instanceof ActivityMediaPlayModel?((ActivityMediaPlayModel)object):null;
    }
}
