package com.merlin.model;


import android.view.View;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.adapter.OnRecyclerScrollStateChange;
import com.merlin.bean.Media;
import com.merlin.bean.Sheet;
import com.merlin.binding.MBinding;
import com.merlin.binding.StatusBar;
import com.merlin.debug.Debug;
import com.merlin.media.MediaPlayer;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.view.OnMultiClick;

public class ActivityMediaPlayModel extends Model implements OnMultiClick,OnPlayerBindChange,OnPlayerStatusUpdate {
    private MediaPlayer mPlayer;
    private final ObservableField<Integer> mStatus=new ObservableField<>();
    private final ObservableField<Media> mPlaying=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter((recyclerView, newState)->{
        if (newState==RecyclerView.SCROLL_STATE_IDLE){
            RecyclerView.Adapter adapter=null!=recyclerView?recyclerView.getAdapter():null;
            if (null!=adapter&&adapter instanceof MediaPlayDisplayAdapter){
                MediaPlayDisplayAdapter ad=((MediaPlayDisplayAdapter)adapter);
                Model model=ad.getCurrentModel();
//                Debug.D(getClass(),"%%%%%% ");
//                String text=null!=model?model.getStatusText():null;
//                if (null!=model){
//
//                }
            }
        }
    });

    public  ActivityMediaPlayModel(){
//        mDisplayAdapter.setModel(this);
    }
    @Override
    public boolean onMultiClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                if (null!=data&&data instanceof Sheet){
                    showMediaSheetDetail((Sheet)data);
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

    public MediaPlayer getPlayer() {
        return mPlayer;
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
        Debug.D(MBinding.class,"aaaaaaaaaa "+view);
        return null;
    }
}
