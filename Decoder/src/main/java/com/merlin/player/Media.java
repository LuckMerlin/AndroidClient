package com.merlin.player;

public abstract class Media<T>  implements Playable{
    private T mSrc;

    public Media(T src){
        mSrc=src;
    }

    public final T getSrc() {
        return mSrc;
    }
}
