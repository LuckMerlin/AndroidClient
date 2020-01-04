package com.merlin.media;

import android.os.Binder;

import com.merlin.player.OnStateUpdate;

import java.util.List;

public abstract class MediaPlayer extends Binder {
    public abstract boolean pause(boolean stop,Object ...obj);
    public abstract boolean start(int position,Object ...obj);
    public abstract boolean play(int index,int position,Object ...obj);
    public abstract int getPlayState();
    public abstract Media getPlaying(Object ...obj);
    public abstract List<Media> getQueue();
    public abstract boolean putListener(OnStateUpdate update);
}