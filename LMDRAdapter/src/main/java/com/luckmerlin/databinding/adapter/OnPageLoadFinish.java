package com.luckmerlin.databinding.adapter;

public interface OnPageLoadFinish<T> {
    void onApiFinish(int what, String note, T data, Object arg);
}
