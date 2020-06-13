package com.merlin.media;

import android.os.Binder;

import com.merlin.player.Media;

import java.util.ArrayList;

public abstract class PlayerBinder extends Binder {
    public abstract Media getPlaying(Object arg, Boolean playing);
    public abstract boolean isPlaying(Object arg,Boolean playing);
    public abstract boolean toggle(int status,Object arg,String debug);
    public abstract long getDuration(Object arg,String debug);
    public abstract long getPosition(Object arg,String debug);
    public abstract ArrayList<Media> getQueue(boolean containPlaying);
}
