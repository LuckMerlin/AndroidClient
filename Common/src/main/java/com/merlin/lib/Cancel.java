package com.merlin.lib;

public class Cancel implements Canceler {
    private boolean mCanceled=false;

    protected void onCancelChange(boolean cancel,String debug){
        //Do nothing
    }

    @Override
    public final boolean cancel(boolean cancel, String debug) {
        if (mCanceled!=cancel){
            mCanceled=cancel;
            onCancelChange(cancel,debug);
            return true;
        }
        return false;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }
}
