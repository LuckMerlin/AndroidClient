package com.merlin.media;

import android.os.Binder;

import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;

import java.util.List;

public abstract class MediaPlayer extends Binder {
    public abstract boolean pause(boolean stop,Object ...obj);
    public abstract boolean play(Object media, float seek, OnPlayerStatusUpdate update);
    public abstract boolean pre();
    public abstract boolean next();
    public abstract Mode playMode(Mode mode);
    public abstract boolean togglePlayPause(Object media);
    public abstract long getDuration();
    public abstract long getPosition();
    public abstract int getPlayState();
    public abstract Playable getPlaying(Object ...obj);
    public abstract List<Playable> getQueue();
    public abstract boolean addListener(OnPlayerStatusUpdate update);
    public abstract boolean removeListener(OnPlayerStatusUpdate update);
}