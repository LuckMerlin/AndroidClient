package com.merlin.transport;

public interface OnStatusChange extends Callback{
    void onStatusChanged(int status, AbsTransport transport);
}
