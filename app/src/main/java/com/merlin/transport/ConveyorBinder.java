package com.merlin.transport;

import java.util.List;

public interface ConveyorBinder  extends Status {
        List<Convey> get(Class<? extends Convey> cls, int ...status);
        boolean listener(int status,OnConveyStatusChange listener,String debug);
        boolean run(int status,String debug,Convey ...conveys);
    }
