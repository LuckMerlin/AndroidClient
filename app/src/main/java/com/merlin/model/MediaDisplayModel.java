package com.merlin.model;

import com.merlin.player.Media;

/**
 * @deprecated
 */
public class MediaDisplayModel extends Model{


    public void onPlayingChange(Media playable){

    }
//    private WeakReference<Playable> mPlaying;
//
//    protected final Playable getPlaying() {
//        WeakReference<Playable> reference=mPlaying;
//        return null!=reference?reference.get():null;
//    }
//
//    public final boolean setPlaying(Playable playable){
//        WeakReference<Playable> playing=mPlaying;
//        mPlaying=null;
//        if (null!=playing){
//            playing.clear();
//        }
//        if (null!=playable){
//            mPlaying=new WeakReference<>(playable);
//        }
//        return true;
//    }


}
