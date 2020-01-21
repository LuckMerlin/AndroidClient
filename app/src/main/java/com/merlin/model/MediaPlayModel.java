package com.merlin.model;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.databinding.ObservableField;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaListAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.FolderMeta;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.media.Media;
import com.merlin.media.MediaPlayer;
import com.merlin.media.Mode;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Player;
import com.merlin.player.Status;
import com.merlin.player1.MPlayer;
import com.merlin.view.OnSeekBarChangeListener;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaPlayModel extends BaseModel implements Status,BaseAdapter.OnItemClickListener<Media>, BaseModel.OnModelViewClick, OnPlayerStatusUpdate {
    private final ObservableField<Media> mPlaying=new ObservableField<>();
    private final ObservableField<Integer> mPlayState=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final ObservableField<Long> mDuration=new ObservableField<>();
    private final ObservableField<Long> mPosition=new ObservableField<>();
    private final ObservableField<Mode> mPlayMode=new ObservableField<>();
    private MediaPlayer mMediaPlayer;
    private final MediaListAdapter mPlayingAdapter;
    private final OnSeekBarChangeListener mSeekListener=new OnSeekBarChangeListener(){
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            MediaPlayer player=fromUser?mMediaPlayer:null;
            if (null!=player){
                player.play(null,progress/100.f,null);
            }
        }
    };

    public MediaPlayModel(Context context){
        super(context);
        mPlayingAdapter=new MediaListAdapter(context);
        mPlayingAdapter.setOnItemClickListener(this);
        updateStatus();
        updateProgress();
        updateMode(null);
    }

    public boolean setMediaPlayer(MediaPlayer player){
        MediaPlayer curr=mMediaPlayer;
        mMediaPlayer=player;
        if (null!=player){
            updateStatus();
            updateProgress();
            updateMode(null);
            player.addListener(this);
            return true;
        }else if (null!=curr){
            curr.removeListener(this);
        }
        return true;
    }

    @Override
    public void onViewClick(View v, int id) {
        MediaPlayer player=mMediaPlayer;
        if (null==player){
            return;
        }
        switch (id){
            case R.id.activityMediaPlay_preIV:
                player.pre();
                break;
            case R.id.activityMediaPlay_playModeIV:
                updateMode(player.playMode(Mode.CHANGE_MODE));
                break;
            case R.id.activityMediaPlay_playPauseIV:
                player.togglePlayPause(null);
                break;
            case R.id.activityMediaPlay_nextIV:
                player.next();
                break;
        }
    }

    private void updateMode(Mode mode){
      MediaPlayer player=null==mode?mMediaPlayer:null;
      mode=null!=player?player.playMode(null):mode;
      mPlayMode.set(mode);
    }

    private void  updateStatus(){
        MediaPlayer player=mMediaPlayer;
        mPlayState.set(null!=player?player.getPlayState():STATUS_UNKNOW);
//        mPlayState.set(null!=player?player.getPlayState():STATUS_UNKNOW);
    }

    @Override
    public void onPlayerStatusUpdated(Player p, int status, String note, Object media, Object data) {
        mPlayState.set(status);
        switch (status){
            case STATUS_PROGRESS:
                updateProgress();
                break;
            default:
                updateStatus();
                break;
        }
    }

    private void updateProgress(){
       MediaPlayer player=mMediaPlayer;
       long duration=0;long position=0;
       if (null!=player){
           duration=player.getDuration();
           position=player.getPosition();
       }
       mDuration.set(duration);
       mPosition.set(position);
       mProgress.set((int)(duration>0?position*100.f/duration:0));
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Media data) {
        MediaPlayer player=null!=data?mMediaPlayer:null;
        if (null!=player){
            player.play(data,0,this);
        }
    }

    public ObservableField<Integer> getPlayState() {
        return mPlayState;
    }

    public MediaListAdapter getPlayingAdapter() {
        return mPlayingAdapter;
    }

    public ObservableField<Media> getPlaying() {
        return mPlaying;
    }

    public OnSeekBarChangeListener getSeekListener() {
        return mSeekListener;
    }

    public ObservableField<Integer> getProgress() {
        return mProgress;
    }

    public ObservableField<Long> getDuration() {
        return mDuration;
    }

    public ObservableField<Long> getPosition() {
        return mPosition;
    }
}
