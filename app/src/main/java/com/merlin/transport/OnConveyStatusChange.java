package com.merlin.transport;

public interface OnConveyStatusChange extends Status {
    void onConveyStatusChanged(int status, Convey convey,Object data);
}
