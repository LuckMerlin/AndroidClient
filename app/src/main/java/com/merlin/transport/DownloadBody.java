package com.merlin.transport;

import okhttp3.ResponseBody;

public abstract class DownloadBody implements retrofit2.Callback<ResponseBody>, Canceler {
    private boolean mCanceled=false;


    @Override
    public final boolean cancel(boolean cancel) {
        boolean curr=mCanceled;
        mCanceled=cancel;
        return curr!=cancel;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }
}
