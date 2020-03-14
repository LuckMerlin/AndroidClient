package com.merlin.socket;

public interface OnResponse extends Callback{
    int NEXT_FRAME=12313;
    Integer onResponse(int what,String note,Frame frame,Frame response,Object arg);
}
