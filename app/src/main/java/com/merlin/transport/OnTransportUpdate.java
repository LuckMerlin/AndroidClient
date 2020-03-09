package com.merlin.transport;

public interface OnTransportUpdate {
    void onTransportUpdate(boolean finish,int what, String note,Object data);
}
