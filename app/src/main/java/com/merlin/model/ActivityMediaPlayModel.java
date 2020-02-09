package com.merlin.model;


import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.adapter.OnRecyclerScrollStateChange;
import com.merlin.bean.Media;
import com.merlin.bean.Sheet;
import com.merlin.binding.MBinding;
import com.merlin.binding.StatusBar;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.MediaPlayer;
import com.merlin.media.Mode;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.view.ContextMenuWindow;
import com.merlin.view.OnMultiClick;

public class ActivityMediaPlayModel extends Model implements OnMultiClick,OnPlayerBindChange,OnPlayerStatusUpdate {
    private MediaPlayer mPlayer;
    private final ObservableField<Integer> mStatus=new ObservableField<>();
    private final ObservableField<Media> mPlaying=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final ContextMenuWindow mPopupWindow=new ContextMenuWindow(true);
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter((recyclerView, newState)->{
        if (newState==RecyclerView.SCROLL_STATE_IDLE){
            RecyclerView.Adapter adapter=null!=recyclerView?recyclerView.getAdapter():null;
            if (null!=adapter&&adapter instanceof MediaPlayDisplayAdapter){
                MediaPlayDisplayAdapter ad=((MediaPlayDisplayAdapter)adapter);
                Model model=ad.getCurrentModel();
                if (null!=model){

                    if (model instanceof MediaPlayModel){
                        Media playing=mPlaying.get();
                        setStatusBar(null!=playing?playing.getTitle():R.string.mediaPlayer,StatusBar.CENTER);
                    }else if (model instanceof MediaDisplaySheetCategoryModel){
                        setStatusBar(R.string.sheet,StatusBar.CENTER);
                    }else if (model instanceof MediaDisplayAllMediasModel){
                        setStatusBar(R.string.all,StatusBar.CENTER);
                    }
                }
            }
        }
    });

    @Override
    public boolean onMultiClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                if (null!=data&&data instanceof Sheet){
                    showMediaSheetDetail((Sheet)data);

                }else{
                    switch (resId){
                        case R.id.activityMediaPlay_playPauseIV:
                            pause_play("After play pause clicked.");
                            break;
                        case R.id.activityMediaPlay_addToSheetIV:
//                            Debug.D(getClass(),"AAAAAAAAAAA "+resId);
                            mPopupWindow.reset(R.string.createSheet);
                            mPopupWindow.showAtLocation(view, Gravity.LEFT,0,0);
                            break;
                        case R.id.activityMediaPlay_playModeIV:
                            break;
                        case R.id.activityMediaPlay_preIV:
                            break;
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
        Media media=new Media();
//            media.setPath("/volume1/music/李善姬 - 人海之中遇见你.mp3");
        media.setPath("/volume1/music/筷子兄弟 - 老男孩.mp3");
        media.setTitle("小酒窝");
        player.play(media, 0, new OnPlayerStatusUpdate() {
            @Override
            public void onPlayerStatusUpdated(Player player, int status, String note, Playable media, Object data) {
//                Debug.D(getClass(),"AAAA "+status+" "+media);
            }
        });
    }

    @Override
    public void onPlayerStatusUpdated(Player player, int status, String note, Playable media, Object data) {
        mStatus.set(status);
        switch (status){
            case STATUS_START:
                updatePlaying("While status start.");
                break;
        }
    }


    private boolean pause_play(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&player.togglePlayPause(null);
    }

    private void updatePlaying(String debug){
        final MediaPlayer player=mPlayer;
        Playable playable=null!=player?player.getPlaying():null;
        Media playing=null!=playable&&playable instanceof Media?(Media)playable:null;
        String title=null!=playing?playing.getTitle():null;
        mPlaying.set(playing);
        setStatusBar(title, StatusBar.CENTER);
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

    public ObservableField<Media> getPlaying() {
        return mPlaying;
    }

    public ObservableField<Integer> getProgress() {
        return mProgress;

    }

    public static ActivityMediaPlayModel getModelFromChild(Model model){
        View view=null!=model?model.getRoot():null;
        ViewParent parent=null!=view?view.getParent():null;
        parent=null!=parent?parent.getParent():null;
        Object object=null!=parent&&parent instanceof View?((View)parent).getTag(R.id.modelBind):null;
        return null!=object&&object instanceof ActivityMediaPlayModel?((ActivityMediaPlayModel)object):null;
    }
}