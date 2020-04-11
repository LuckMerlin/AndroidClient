package com.merlin.conveyor;

import com.merlin.api.Canceler;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;

public interface Convey {
    Canceler onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug);
}
