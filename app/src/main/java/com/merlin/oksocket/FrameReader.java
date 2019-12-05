package com.merlin.oksocket;

import com.xuhao.didi.core.protocol.IReaderProtocol;

import java.nio.ByteOrder;

public final class FrameReader implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return HeadReader.HEAD_LENGTH;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        if (null==header||header.length< HeadReader.HEAD_LENGTH) {
            return -1;
        }
        HeadReader head= HeadReader.read(header,null==byteOrder||byteOrder== ByteOrder.BIG_ENDIAN);
//        Debug.D(getClass(),"总长  "+headLength+" "+contentLength
//               +" || "+ Protocol.dumpBytes(header,false) );
        if(null==head){
            throw new RuntimeException("Body build NONE.");
        }
//        long total = headLength+contentLength;
//        Debug.D(getClass(),"总长度 "+total);
        return head.getBodySize();
    }

}
