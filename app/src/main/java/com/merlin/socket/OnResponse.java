package com.merlin.socket;

public interface OnResponse extends Callback{
    void onResponse(int what,String note,Frame frame,Frame response,Object arg);
}
