package com.merlin.transport;

import java.util.Collection;

public interface TransportBinder extends Callback {
    Collection<? extends AbsTransport> getRunning(int type);
    boolean callback(int status,Callback callback);
    boolean run(int status, AbsTransport transport, boolean interactive, String debug);
}
