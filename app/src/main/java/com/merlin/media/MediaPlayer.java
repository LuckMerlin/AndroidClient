package com.merlin.media;

import android.os.Binder;

import com.merlin.player.OnStateUpdate;

import java.util.List;

public abstract class MediaPlayer extends Binder {
    public abstract boolean pause(boolean stop,Object ...obj);
    public abstract boolean play(Object media,float seek,OnStateUpdate update);
    public abstract int getPlayState();
    public abstract Media getPlaying(Object ...obj);
    public abstract List<Media> getQueue();
    public abstract boolean putListener(OnStateUpdate update);
}