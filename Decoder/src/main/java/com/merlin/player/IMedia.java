package com.merlin.player;

public abstract class IMedia<T>  implements Playable{
    private T mSrc;

    public IMedia(T src){
        mSrc=src;
    }

    public final T getSrc() {
        return mSrc;
    }
}
