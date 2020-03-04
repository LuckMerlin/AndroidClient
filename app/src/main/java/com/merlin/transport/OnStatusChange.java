package com.merlin.transport;

public interface OnStatusChange extends Callback{
    void onStatusChanged(int status,Transport transport);
}
