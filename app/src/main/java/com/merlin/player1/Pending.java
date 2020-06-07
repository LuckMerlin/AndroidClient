package com.merlin.player1;

import com.merlin.player.Playable;

final class Pending {
     private final Playable mMedia;
     private final double mSeek;
     private final String mDebug;

     public Pending(Playable playable,double seek,String debug){
         mMedia=playable;
         mSeek=seek;
         mDebug=debug;
     }

    public double getSeek() {
        return mSeek;
    }

    public Playable getMedia() {
        return mMedia;
    }

    public String getDebug() {
        return mDebug;
    }
}
