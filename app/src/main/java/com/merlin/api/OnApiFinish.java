package com.merlin.api;

/**
 * @deprecated
 * @param <T>
 */
public interface OnApiFinish<T> extends Callback<T>{
    void onApiFinish(int what,String note,T data,Object arg);
}
