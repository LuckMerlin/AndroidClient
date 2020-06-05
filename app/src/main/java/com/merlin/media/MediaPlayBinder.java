package com.merlin.media;

import android.os.Binder;

import com.merlin.player.OnPlayerStatusChange;
import com.merlin.player.Playable;

public abstract class MediaPlayBinder extends Binder {
    public abstract Playable getPlaying(Object arg,Boolean playing);
    public abstract boolean isPlaying(Object arg,Boolean playing);
    public abstract boolean toggle(int status,Object arg,String debug);
    public abstract boolean listener(int status, OnPlayerStatusChange change, String debug);
}
