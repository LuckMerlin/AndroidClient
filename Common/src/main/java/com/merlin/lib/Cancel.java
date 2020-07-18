package com.merlin.lib;

public class Cancel implements Canceler {
    private boolean mCanceled=false;

    @Override
    public final boolean cancel(boolean cancel, String debug) {
        if (mCanceled!=cancel){
            mCanceled=cancel;
            return true;
        }
        return false;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }
}
