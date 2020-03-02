package com.merlin.transport;

import java.util.Collection;

public interface TransportBinder {
    Collection<? extends Transport> getRunning(int type);
    boolean add(Transporter.Callback progress);
    boolean remove(Transporter.Callback progress);
}
