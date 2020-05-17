package com.merlin.model;


import android.view.View;
import android.view.ViewParent;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.adapter.MediaPlayingQueueAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasMedia;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.client.databinding.MediaPlayingQueueBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.media.AddToSheetApi;
import com.merlin.media.FavoriteApi;
import com.merlin.media.MediaPlayer;
import com.merlin.media.Mode;
import com.merlin.player.BK_Player;
import com.merlin.player.FileBuffer;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.IPlayable;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.player.Time;
import com.merlin.player1.LPlayer;
import com.merlin.view.OnSeekBarProgressChange;
import com.merlin.view.OnTapClick;
import com.merlin.view.Res;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ActivityMediaPlayModel extends Model implements OnTapClick, What, Label,OnPlayerBindChange,OnPlayerStatusUpdate {
    private MediaPlayer mPlayer;
    private final ObservableField<Integer> mStatus=new ObservableField<>();
    private final ObservableField<Integer> mMode=new ObservableField<>();
    private final ObservableField<Boolean> mFavorite=new ObservableField<>();
    private final ObservableField<NasMedia> mPlaying=new ObservableField<>();
    private final ObservableField<Integer> mProgress=new ObservableField<>();
    private final ObservableField<Object> mAlbumImage=new ObservableField<>();
    private final ObservableField<String> mCurrPosition=new ObservableField<>();
    private final ObservableField<String> mPlayingMeta=new ObservableField<>();
    private final ObservableField<String> mPlayingArtistAlbum=new ObservableField<>();
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter();
    private final OnSeekBarProgressChange mOnSeekChange=(seekBar, progress, fromUser)-> {
        MediaPlayer player=fromUser?mPlayer:null;
        if (null!=player){
            player.seek(progress/100.f,"After seekBar tap click.");
        }
    };

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dd();
            }
        }).start();
    }

    private void dd(){
        try {
            FileInputStream fis=new FileInputStream("/sdcard/Musics/大壮 - 我们不一样.mp3");
//            FileInputStream fis=new FileInputStream("/storage/sdcard0/gn.mp3");
            new LPlayer().play(new Playable() {
                @Override
                public int read(byte[] buffer) {
                    if (null!=buffer&&buffer.length>0){
                        try {
                            return fis.read(buffer);
                        } catch (IOException e) {
                            Debug.D(getClass(),"ddd "+e);
                            e.printStackTrace();
                        }
                    }
                    return BUFFER_READ_FINISH_NORMAL;
                }

                @Override
                public boolean open() {
                    return false;
                }

                @Override
                public boolean close() {
                    return false;
                }
            },0);
        } catch (FileNotFoundException e) {
            Debug.D(getClass(),""+e);
            e.printStackTrace();
        }
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
                            return pause_play("After play_pause tap click.")||true;
                        case R.drawable.single_normal:
                        case R.drawable.random_normal:
                        case R.drawable.list_sort_normal:
                            return changePlayMode("After mode tap click.")||true;
                        case R.drawable.selector_pre:
                            return pre("After pre media tap click.")||true;
                        case R.drawable.selector_menu:
                            return showPlayingQueue("After menu tap click.")||true;
                        case R.drawable.selector_add_to_sheet:
                            return addToSheet("After add sheet tap click.")||true;
                        case R.drawable.selector_next:
                            return next("After next media tap click.")||true;
//                        case R.drawable.selector_download_media:
//                            NasMedia media=mPlaying.get();
//                            return null!=media&&TransportService.download(getContext(),media,"After play display download tap.")||true;
                        case R.drawable.heart_pressed://Get through
                        case R.drawable.heart_normal://get through
                            return (null!=view&&makeFavorite(resId!=R.drawable.heart_pressed))||true;
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
            updatePlaying(null,"After player bind.");
            updatePlayMode(null,"After player bind.");
            player.addListener(this);
        }
    }

    @Override
    public void onPlayerStatusUpdated(BK_Player player, int status, String note, IPlayable media, Object data) {
        mStatus.set(status);
        switch (status){
            case STATUS_START: updatePlaying(null,"While status start.");break;
            case STATUS_STOP:updatePlaying(null,"While status stop.");break;
            case STATUS_IDLE:updatePlaying(null,"While status idle.");break;
            case STATUS_PROGRESS:
                float progress=null!=player?player.getCurrentProgress():0;
                progress=progress>=0&&progress<=1?progress:0;
                long duration=player.getDuration();
                if (duration>0){
                    mCurrPosition.set(Time.formatTime((long)(progress*duration)));
                }
                mProgress.set((int)(progress*100));
                break;
        }
    }

    private boolean updatePlayMode(Mode mode,String debug){
        MediaPlayer player=mPlayer;
        mode = null==mode&&null!=player?player.playMode(null):mode;
        int id= R.drawable.single_normal;
        if (null!=mode){
            switch (mode){
                case SINGLE:
                    id=R.drawable.single_normal;break;
                case RANDOM:
                    id=R.drawable.random_normal;break;
                case QUEUE_SORT:
                    id=R.drawable.list_sort_normal;break;
            }
        }
        mMode.set(id);
        return true;
    }

    private boolean addToSheet(String debug){
        NasMedia playing=mPlaying.get();
        String md5=null!=playing?playing.getMd5():null;
        if (null!=md5&&md5.length()>0){
            Dialog dialog=new Dialog(getViewContext());
            ViewDataBinding binding=inflate(R.layout.media_sheet_choose,new Res(com.merlin.client.BR.media,playing));
            return dialog.setContentView(binding,true).title(R.string.addToSheet).left(R.string.create).right(R.string.cancel).
                    show((view,clickCount,resId,data)->{
                        dialog.dismiss();
                        String sheetId=null!=data&&data instanceof Sheet?((Sheet)data).getId():null;
                        if (null!=sheetId&&sheetId.length()>0){
                            return null!=call(prepare(AddToSheetApi.class, Address.HOST).addIntoSheet(md5,sheetId),(OnApiFinish<Reply<NasMedia>>)(what, note, m, arg)->{
                                toast(note);
                            })||true;
                        }
                        return false;},false);
        }
        return false;
    }

    private boolean changePlayMode(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&updatePlayMode(player.playMode(Mode.CHANGE_MODE),debug);
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
        MediaPlayer player=mPlayer;
        List<IPlayable> playing=null!=player?player.getQueue():null;
        final int size=null!=playing?playing.size():-1;
        if (size<=0){
            return toast(R.string.listEmpty)&&false;
        }
        MediaPlayingQueueBinding binding=inflate(R.layout.media_playing_queue);
        if (null!=binding){
            Dialog dialog=new Dialog(getViewContext());
            MediaPlayingQueueAdapter adapter=new MediaPlayingQueueAdapter(playing);
            binding.setAdapter(adapter);
            String title=getText(R.string.playing);
            title=null!=title?title+" ("+size+")":null;
            return dialog.setContentView(binding,true).title(title).show();
        }
        return false;
    }

    private boolean makeFavorite(boolean favorite){
        final NasMedia playing=mPlaying.get();
        String md5=null!=playing?playing.getMd5():null;
        if (null==md5||md5.length()<=0){
            return false;
        }
        Debug.D(getClass(),"favorite "+favorite);
        return null!=call(prepare(FavoriteApi.class,Address.HOST).makeFavorite(md5,favorite),(OnApiFinish<Reply<NasFile>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED&&null!=data){
                playing.setFavorite(favorite);
                updatePlaying(playing,"After favorite succeed.");
            }else{
                toast(note);
            }
        });
    }

    private boolean pause_play(String debug){
        MediaPlayer player=mPlayer;
        return null!=player&&player.togglePlayPause(null);
    }

    private void updatePlaying(NasMedia media,String debug){
        NasMedia playing=media;
        if (null==playing){
            MediaPlayer player=mPlayer;
            IPlayable playable=null!=player?player.getPlaying():null;
            playing=null!=playable&&playable instanceof NasMedia ?(NasMedia)playable:null;
        }
        mCurrPosition.set(Time.formatTime(0));
        mProgress.set(0);
        mPlaying.set(playing);
        mFavorite.set(null!=playing&&playing.isFavorite());
        String imageUrl=null!=playing?playing.getThumbImageUrl():null;
        mAlbumImage.set(null!=imageUrl&&imageUrl.length()>0?imageUrl:R.drawable.album_default);
        int sampleRate=null!=playing?playing.getSampleRate():-1;
        String bitrateMode=null!=playing?playing.getBitrateMode():null;
        String meta=sampleRate>0?(sampleRate/1000.f)+"KHZ ":"";
        meta=null!=bitrateMode?meta+bitrateMode:"";
        String artist=null!=playing?playing.getArtist():"";
        String album=null!=playing?playing.getAlbum():"";
        mPlayingArtistAlbum.set((null!=artist?artist:"")+"\n "+(null!=album?album:""));
        mPlayingMeta.set(meta);
        MediaPlayDisplayAdapter adapter=mDisplayAdapter;
        if (null!=adapter){
            adapter.setPlaying(playing);
        }
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

    public ObservableField<Integer> getCurrentProgress() {
        return mProgress;
    }

    public OnSeekBarProgressChange getOnSeekChange() {
        return mOnSeekChange;
    }

    public ObservableField<String> getCurrentPosition() {
        return mCurrPosition;
    }

    public ObservableField<Boolean> getFavorite() {
        return mFavorite;
    }

    public ObservableField<Integer> getMode() {
        return mMode;
    }

    public ObservableField<Object> getAlbumImage() {
        return mAlbumImage;
    }

    public ObservableField<String> getPlayingMeta() {
        return mPlayingMeta;
    }

    public ObservableField<String> getPlayingArtistAlbum() {
        return mPlayingArtistAlbum;
    }

    public static ActivityMediaPlayModel getModelFromChild(Model model){
        View view=null!=model?model.getRoot():null;
        ViewParent parent=null!=view?view.getParent():null;
        parent=null!=parent?parent.getParent():null;
        Object object=null!=parent&&parent instanceof View?((View)parent).getTag(R.id.modelBind):null;
        return null!=object&&object instanceof ActivityMediaPlayModel?((ActivityMediaPlayModel)object):null;
    }
}
