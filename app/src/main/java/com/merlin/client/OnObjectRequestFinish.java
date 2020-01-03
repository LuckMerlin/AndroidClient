package com.merlin.client;

import com.merlin.server.Frame;

public interface OnObjectRequestFinish<T> {
    void onObjectRequested(boolean succeed, int what, String note, Frame frame, T data);
}
