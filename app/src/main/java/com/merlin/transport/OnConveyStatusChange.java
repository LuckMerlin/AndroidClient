package com.merlin.transport;

import com.merlin.api.Reply;
import com.merlin.conveyor._Convey;

public interface OnConveyStatusChange extends Status {
    void onConveyStatusChanged(int status, _Convey convey, Reply reply);
}
