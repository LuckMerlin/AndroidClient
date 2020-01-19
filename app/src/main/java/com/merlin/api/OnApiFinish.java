package com.merlin.api;

public interface OnApiFinish<T> extends Callback<T>{
    void onApiFinish(int what,String note,T data,Object arg);
}
