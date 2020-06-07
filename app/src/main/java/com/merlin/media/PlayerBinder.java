package com.merlin.media;

import android.os.Binder;

import com.merlin.player.OnPlayerStatusChange;
import com.merlin.player.Playable;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerBinder extends Binder {
    public abstract Playable getPlaying(Object arg,Boolean playing);
    public abstract boolean isPlaying(Object arg,Boolean playing);
    public abstract boolean toggle(int status,Object arg,String debug);
    public abstract long getDuration(Object arg,String debug);
    public abstract long getPosition(Object arg,String debug);
    public abstract ArrayList<Playable> getQueue(boolean containPlaying);
}
