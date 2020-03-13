package com.merlin.socket;

import androidx.annotation.NonNull;

import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public final class HeartBeater implements IPulseSendable {
    private final byte[] mHeartbeatBytes=new Frame(Frame.FORMAT_HEART_BEATER).toFrameBytes();

    @Override
    public byte[] parse() {
        return mHeartbeatBytes;
    }

    @NonNull
    @Override
    public String toString() {
        return ""+(null!=mHeartbeatBytes?mHeartbeatBytes.length:-1)+" "+super.toString();
    }
}
