package com.merlin.transport;
import androidx.annotation.Nullable;

import com.merlin.debug.Debug;
import com.merlin.file.CoverMode;
import com.merlin.server.Retrofit;
import com.merlin.util.StringEquals;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.dispatcher.IRegister;

public abstract class Transport<T extends Retrofit.Canceler> implements CoverMode,Callback {
    private final String mName;
    private long mTotal;
    private long mSize;
    private float mSpeed;
    private boolean mCancel=false;
    private final int mCoverMode;
    private final String mFromPath;
    private final String mToFolder;
    private String mError;

    public Transport(String fromPath, String toFolder, String name, Integer coverMode){
        mFromPath=fromPath;
        mToFolder=toFolder;
        mName=name;
        mCoverMode=null!=coverMode?coverMode:CoverMode.COVER_MODE_NONE;
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

    public final String getTitle(){
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

    protected void onCancelChanged(boolean cancel){
        //DO nothing
    }
    public final boolean cancel(boolean cancel){
        boolean curr=mCancel;
        if (curr!=cancel) {
            mCancel = cancel;
            onCancelChanged(cancel);
            return true;
        }
        return false;
    }

    public final boolean isCancel() {
        return mCancel;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof Transport){
            StringEquals equals=new StringEquals();
            Transport tp=(Transport)obj;
            return equals.equals(mFromPath,tp.mFromPath)&&equals.equals(mToFolder,tp.mToFolder)
                    &&equals.equals(mName,tp.mName);
        }
        return super.equals(obj);
    }
}
