package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

public class IIBuffer {
    private final byte[] mBuffer=new byte[8192*3];
    public final static int EOF = -1;
    public final static int FATAL_ERROR = -2;//Keep not change for native
    public final static int NORMAL = 0;//Keep not change for native
    public final static int EMPTY=-4;
    public final static int FULL=-5;
    private int mCursor=0;

    final void notifyRead(String debug){
        byte[] buffer=mBuffer;
        synchronized (buffer){
            Debug.D(getClass(),"Notify buffer read "+(null!=debug?debug:"."));
            buffer.notify();
        }
    }

    protected Integer onLoadBytes(int length){
        //Do nothing
        return null;
    }

    final int read(byte[] buffer,int offset) {
        int size=null!=buffer?buffer.length:-1;
        int emptyLength=offset>=0&&size>0&&offset<size?size-offset:-1;
        if (emptyLength < 0){
            return FATAL_ERROR;
        }
        if (emptyLength == 0){
            return NORMAL;
        }
        while (true) {
            byte[] innerBuffer = mBuffer;
            synchronized (innerBuffer) {
                int length = innerBuffer.length;
                int cursor = mCursor;
                if (length <= 0 || cursor < 0 || cursor > length) {
                    return FATAL_ERROR;
                }
                if (cursor == 0) {//Empty
                    Integer load=onLoadBytes(length);
                    if (null==load){
                        return IIBuffer.FATAL_ERROR;
                    }
                    if (load!= IIBuffer.NORMAL){
                        try {
                            Debug.D(getClass(), "Wait writing bytes after byte buffer empty.");
                            innerBuffer.wait();
                            Debug.D(getClass(), "Read is wake up.");
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FATAL_ERROR;
                        }
                    }
                    continue;
//                        Integer load=onLoadBytes(mConnect);//Try load bytes
//                        if (null==load){
//                            Debug.D(getClass(), "Wait writing bytes while read is empty or read normal.");
//                            innerBuffer.wait();
//                            Debug.D(getClass(), "Read is wake up.");
//                            continue;
//                        }
//                        if (load >= 0){
//                            mCursor += load;
//                            return read(buffer,offset);
//                        }
//                        return load;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return FATAL_ERROR;
//                    }
                }
                int available=Math.min(emptyLength,cursor);//Not empty
                System.arraycopy(innerBuffer,0,buffer,offset,available);
                mCursor-=available;
                return available;
            }
        }
    }

    final int write(InputStream stream,long skip){
        if (null==stream){
            return FATAL_ERROR;
        }
        if (skip>0){
            try {
                stream.skip(skip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int totalRead=0;
        while (true) {
            byte[] innerBuffer = mBuffer;
            synchronized (innerBuffer) {
                int length=innerBuffer.length;
                int cursor=mCursor;
                if (cursor<0||cursor>length){
                    return FATAL_ERROR;
                }
                if (cursor==length){//Full
                    return totalRead;
                }
                try {
                    int read=stream.read(innerBuffer,cursor,length-cursor);
                    if (read>0) {
                        mCursor += read;
                        totalRead+=read;
                        continue;
                    }
                    return totalRead;
                } catch (IOException e) {
                    e.printStackTrace();
                    return FATAL_ERROR;
                }
            }
        }
    }

}
