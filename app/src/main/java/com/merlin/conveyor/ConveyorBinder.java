package com.merlin.conveyor;

import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.Status;

import java.util.List;

public interface ConveyorBinder  extends Status {
        List<Convey> get(Class<? extends Convey> cls, int ...status);
        boolean listener(int status, OnConveyStatusChange listener, String debug);
        boolean run(int status,String debug,Convey ...conveys);
    }
