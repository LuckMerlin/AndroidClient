package com.merlin.player;

public class Reader {
    private long mRead=0;
    private final byte[] mBuffer=new byte[1024];

    protected int onLoad(byte[] buffer,int offset,int size){
        return -1;
    }

    public boolean read(byte[] buffer,int offset,int size) {
        int volume = null != buffer ? buffer.length : -1;
        if (volume <= 0 || offset < 0 || size <= 0 || (offset + size > volume)) {
            return false;
        }
        byte[] innerBuffer=mBuffer;
        if (null==innerBuffer||innerBuffer.length<=0){

        }

//        int read=onLoad(buffer,offset,size);
        return false;
    }
}
