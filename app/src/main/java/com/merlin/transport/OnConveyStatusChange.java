package com.merlin.transport;

import com.merlin.conveyor.Convey;

public interface OnConveyStatusChange extends Status {
    void onConveyStatusChanged(int status, Convey convey, Object data);
}
