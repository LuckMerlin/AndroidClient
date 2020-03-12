package com.merlin.api;

public interface OnApiFinish<T> extends Callback {
    void onApiFinish(int what,String note,T data, Object arg);
}
