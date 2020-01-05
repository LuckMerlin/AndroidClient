package com.merlin.player;

public interface MediaBuffer {

    interface Canceler{
        boolean cancel(boolean interrupt);
    }

    interface OnMediaBufferFinish{
        int BUFFER_SUCCEED=13131;
        int BUFFER_FAILED=13132;
        void onMediaBufferFinish(boolean succeed, int what, String account, String url, String target);
    }
    Canceler buffer(String account, String url, String target, OnMediaBufferFinish callback);
}
