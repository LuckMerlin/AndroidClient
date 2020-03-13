package com.merlin.socket;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public final class HeartBeater implements IPulseSendable {
    private final byte[] mHeartbeatBytes=new Frame(Frame.FORMAT_HEART_BEATER).toFrameBytes();

    @Override
    public byte[] parse() {
        return mHeartbeatBytes;
    }

}
