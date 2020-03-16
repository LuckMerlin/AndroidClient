package com.merlin.socket;

import com.merlin.debug.Debug;

public class Canceler {
    private boolean mCancel;

    protected Canceler(){

    }

    public final boolean cancel(boolean cancel,String debug){
        boolean curr=mCancel;
        if (curr!=cancel){
            mCancel=cancel;
            Debug.D(getClass(),"Cancel "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    public final boolean isCancel() {
        return mCancel;
    }
}
