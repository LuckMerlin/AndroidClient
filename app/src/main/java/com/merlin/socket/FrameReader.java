package com.merlin.socket;

import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

public class FrameReader  implements IReaderProtocol {

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        return 0;
    }

    @Override
    public int getHeaderLength() {
        return 0;
    }

}
