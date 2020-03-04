package com.merlin.transport;

import com.merlin.bean.ClientMeta;
import com.merlin.file.CoverMode;

public abstract class Transport implements CoverMode {
    private final String mName;
    private long mTotal;
    private long mSize;
    private float mSpeed;
    private final ClientMeta mClient;
    private final int mCoverMode;

    public Transport(String name,ClientMeta client,Integer coverMode){
        mName=name;
        mCoverMode=null!=coverMode?coverMode:CoverMode.COVER_MODE_NONE;
        mClient=client;
    }

    public final String getName() {
        return mName;
    }

    public String getTitle(){
        return getName();
    }

    public final long getTotal() {
        return mTotal;
    }

    public final long getSize() {
        return mSize;
    }

    public final float getSpeed() {
        return mSpeed;
    }

    public final ClientMeta getClient() {
        return mClient;
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    public final int getCoverMode() {
        return mCoverMode;
    }

    public void setTotal(long mTotal) {
        this.mTotal = mTotal;
    }
}
