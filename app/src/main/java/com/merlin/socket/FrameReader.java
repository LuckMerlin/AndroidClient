package com.merlin.socket;

import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

public class FrameReader  implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return Frame.LENGTH_BYTES_SIZE<<1;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        return 0;
    }

}
