package com.merlin.transport;

import com.merlin.debug.Debug;

public class Canceler {
    private boolean mCanceled;
    public Canceler(){
        this(false);
    }
    public Canceler(boolean canceled){
        mCanceled=canceled;
    }
    public final boolean isCanceled() {
        return mCanceled;
    }

    public final boolean cancel(boolean cancel,String debug) {
        boolean curr=mCanceled;
        if (curr!=cancel){
            mCanceled=cancel;
            Debug.D(getClass(),"Cancel "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }
}
