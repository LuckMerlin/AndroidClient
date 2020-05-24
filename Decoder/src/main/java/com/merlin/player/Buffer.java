package com.merlin.player;
public abstract class Buffer implements Playable{
     private final byte[] mBuffered;
     private int mSize;

     public Buffer(int bufferSize){
         mBuffered=bufferSize>0?new byte[bufferSize]:null;
         resetBuffer();
     }

     public final void resetBuffer(){
         mSize=0;
     }

     protected abstract int onReadBytes(long start, int offset, byte[] buffer,int size);

    @Override
    public final int read(long start, int offset, byte[] buffer) {
        final int bufferSize=null!=buffer?buffer.length:-1;
        if (bufferSize<=0){
            return Status.STATUS_NORMAL;
        }
        offset=offset<=0?0:offset;
        int preferSize=bufferSize-offset;
        if (preferSize<=0){
            return Status.STATUS_NORMAL;
        }
        byte[] buffered=mBuffered;
        int volume=null!=buffered?buffered.length:-1;
        if (volume>0) {//If buffer enabled
            int size = mSize;
            int availableSize=size>0?volume-size:-1;
            if (availableSize>0) { //If exist buffered data
                int copyLength=Math.min(preferSize,availableSize);
                System.arraycopy(buffered,0,buffer,offset,copyLength);
                if ((size=(availableSize-copyLength))>0) {
                    System.arraycopy(buffered, availableSize, buffered, 0, size);
                }
                mSize=size;
                return copyLength;
            }
        }
        int readSize=onReadBytes(start,offset,buffer,preferSize);
        if (readSize<=0){

        }
        return readSize;
    }
}
