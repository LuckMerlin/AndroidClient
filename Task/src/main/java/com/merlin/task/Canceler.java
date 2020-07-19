package com.merlin.task;

public final class Canceler {
    private boolean mCanceled=false;

    public final boolean cancel(Boolean cancel,String debug){
        if (null!=cancel){
            if (cancel!=mCanceled){
                mCanceled=cancel;
                return true;
            }
            return false;
        }
        return mCanceled;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }
}
