package com.merlin.transport;

import java.util.Collection;

public interface TransportBinder extends Transporter.Callback {
    Collection<? extends Transport> getRunning(int type);
    boolean callback(int status,Transporter.Callback callback);
    boolean run(int status,Transport transport,String debug);
}
