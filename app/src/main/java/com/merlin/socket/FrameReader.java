package com.merlin.socket;

import com.merlin.debug.Debug;
import com.merlin.util.Int;
import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

public final class FrameReader  implements IReaderProtocol {
    private final static int MIN_FRAME_LENGTH =Frame.LENGTH_BYTES_SIZE<<1;

    @Override
    public int getHeaderLength() {
        return MIN_FRAME_LENGTH;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (null!=header&&header.length>=MIN_FRAME_LENGTH){
            Integer frameHeadLength = Int.toInt(header, 0, null);
            Integer frameBodyLength = Int.toInt(header, Frame.LENGTH_BYTES_SIZE, null);
            if (null == frameHeadLength || null == frameBodyLength || frameBodyLength < 0 || frameHeadLength < 0) {
                throw new RuntimeException("Invalid frame.");
            }
            return frameHeadLength+frameBodyLength;
        }
        return 0;
    }

}
