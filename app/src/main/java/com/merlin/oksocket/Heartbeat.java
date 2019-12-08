package com.merlin.oksocket;

import com.merlin.protocol.Protocol;
import com.merlin.protocol.Tag;
import com.merlin.server.Frame;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;

public final class Heartbeat  implements IPulseSendable {
    private byte[] mHeartbeatBytes=Protocol.generateFrame(null,Frame.encodeString(Tag.TAG_HEART_BEAT,"utf-8"),null);

    @Override
    public byte[] parse() {
        return mHeartbeatBytes;
    }

}
