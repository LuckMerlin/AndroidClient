package com.merlin.transport;

import com.merlin.bean.ClientMeta;
import com.merlin.server.Retrofit;

public abstract class AbsTransport<T extends Canceler> extends Transport<T> {
    private final ClientMeta mClient;
    private Transport mTransporting;

    public AbsTransport(String fromPath, String toFolder, String name, ClientMeta client, Integer coverMode){
      super(fromPath,toFolder,name,coverMode);
        mClient=client;
    }

    public final ClientMeta getClient() {
        return mClient;
    }

    public final Transport getTransporting() {
        return mTransporting;
    }

    protected final void setTransporting(Transport transport){
        mTransporting=transport;
    }

    protected abstract T onStart(OnTransportUpdate update, Retrofit retrofit);
}
