package com.merlin.transport;

import com.merlin.api.Reply;
import com.merlin.conveyor.Convey;

public interface OnConveyStatusChange<T> extends Status {
    void onConveyStatusChanged(int status, Convey convey, Reply<T> reply);
}
