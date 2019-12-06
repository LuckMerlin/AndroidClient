package com.merlin.oksocket;

import com.merlibn.global.Protocol;
import com.merlibn.global.Tag;
import com.merlin.server.Frame;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public final class Heartbeat  implements IPulseSendable {
    private byte[] mHeartbeatBytes=Protocol.generateFrame(Frame.encodeString(Tag.TAG_HEART_BEAT,"utf-8"),null);

    @Override
    public byte[] parse() {
        return mHeartbeatBytes;
    }

}
