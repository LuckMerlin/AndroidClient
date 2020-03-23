package com.merlin.transport;

import java.util.Collection;

public interface ConveyorBinder  extends Status {
        Collection<Convey> get(Class<? extends Convey> cls,int ...status);
        boolean listener(int status,OnConveyStatusChange listener,String debug);
        boolean run(int status,String debug,Convey ...conveys);
    }
