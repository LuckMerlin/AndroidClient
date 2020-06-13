package com.merlin.player1;

import com.merlin.player.Media;

final class Pending {
     private final Media mMedia;
     private final double mSeek;
     private final String mDebug;

     public Pending(Media playable, double seek, String debug){
         mMedia=playable;
         mSeek=seek;
         mDebug=debug;
     }

    public double getSeek() {
        return mSeek;
    }

    public Media getMedia() {
        return mMedia;
    }

    public String getDebug() {
        return mDebug;
    }
}
