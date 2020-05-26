package com.merlin.player;

public class Reader {
    private long mRead=0;

    protected int onLoad(byte[] buffer,int offset,int size){
        return -1;
    }

    public boolean read(byte[] buffer,int offset,int size) {
        int volume = null != buffer ? buffer.length : -1;
        if (volume <= 0 || offset < 0 || size <= 0 || (offset + size > volume)) {
            return false;
        }
//        int read=onLoad(buffer,offset,size);
        return false;
    }
}
