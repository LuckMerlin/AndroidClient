package com.merlin.transport;

import com.merlin.bean.ClientMeta;
import com.merlin.file.CoverMode;
import com.merlin.server.Retrofit;

public abstract class Transport<T extends Canceler> implements CoverMode,Callback {
    private final String mName;
    private long mTotal;
    private long mSize;
    private float mSpeed;
    private final ClientMeta mClient;
    private final int mCoverMode;
    private final String mFromPath;
    private final String mToFolder;
    private String mError;

    public Transport(String fromPath,String toFolder,String name,ClientMeta client,Integer coverMode){
        mFromPath=fromPath;
        mToFolder=toFolder;
        mName=name;
        mCoverMode=null!=coverMode?coverMode:CoverMode.COVER_MODE_NONE;
        mClient=client;
    }

    public void setError(String error) {
        this.mError = error;
    }

    public String getError() {
        return mError;
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

    public final String getFromPath() {
        return mFromPath;
    }

    public final String getToFolder() {
        return mToFolder;
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

    protected abstract T onStart(OnTransportUpdate update, Retrofit retrofit);
}
