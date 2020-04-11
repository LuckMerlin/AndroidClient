package com.merlin.transport;

import java.util.Collection;

/**
 * @deprecated
 */
public interface TransportBinder extends Callback {
    Collection<? extends AbsTransport> getRunning(int type);
    boolean callback(int status,Callback callback);
    boolean run(int status, boolean interactive, String debug,AbsTransport ...transports);
}
