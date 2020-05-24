package com.merlin.player;

public abstract class Media<T> extends Buffer implements Playable{
    private T mSrc;

    public Media(T src,int bufferSize){
        super(bufferSize);
        mSrc=src;
    }

    public final T getSrc() {
        return mSrc;
    }
}
