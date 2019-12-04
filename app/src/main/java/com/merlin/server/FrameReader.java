package com.merlin.server;

import com.merlin.debug.Debug;
import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;
import java.util.Arrays;

public final class FrameReader implements IReaderProtocol {
    private final static int MIN_LENGTH =7; //1+1+5

    @Override
    public int getHeaderLength() {
        return MIN_LENGTH;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (null==header||header.length<MIN_LENGTH) {
            return -1;
        }
        byte code=header[0];
        byte encoding=header[1];
        int head_length= Arrays.copyOfRange(header,2,MIN_LENGTH);
        String ddd=" code="+code+" encoding="+encoding+" ";
        if(null!=header){
            for (byte b:header){
                ddd+=b+" ";
            }
        }
        Debug.W(getClass(),ddd);
        return 0;
    }

}
