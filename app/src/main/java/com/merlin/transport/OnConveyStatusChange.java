package com.merlin.transport;

public interface OnConveyStatusChange {
    void onConveyStatusChanged(int status, Convey convey,Object data);
}
