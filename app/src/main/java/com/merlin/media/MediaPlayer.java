package com.merlin.media;

import android.os.Binder;

import com.merlin.player.IPlayable;
import com.merlin.player.OnPlayerStatusUpdate;

import java.util.List;

public abstract class MediaPlayer extends Binder {
    public abstract boolean pause(boolean stop,Object ...obj);
    public abstract boolean play(Object media, float seek, OnPlayerStatusUpdate update);
    public abstract boolean pre(String debug);
    public abstract boolean next(String debug);
    public abstract Mode playMode(Mode mode);
    public abstract boolean togglePlayPause(Object media);
    public abstract long getDuration();
    public abstract long getPosition();
    public abstract int getPlayState();
    public abstract boolean seek(double seek,String debug);
    public abstract IPlayable getPlaying(Object ...obj);
    public abstract List<IPlayable> getQueue();
    public abstract boolean addListener(OnPlayerStatusUpdate update);
    public abstract boolean removeListener(OnPlayerStatusUpdate update);
}