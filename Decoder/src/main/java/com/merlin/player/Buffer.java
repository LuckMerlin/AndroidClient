package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.IOException;
import java.io.InputStream;

public class Buffer {
    private final byte[] mBuffer=new byte[1024];
    public final static int EOF = -1;
    public final static int FATAL_ERROR = -2;//Keep not change for native
    public final static int NORMAL = -3;//Keep not change for native
    public final static int EMPTY=-4;
    public final static int FULL=-5;
    private int mCursor=0;

    protected Integer onLoadBytes(byte[] buffer,int offset) throws IOException {
        return null;
    }

    final void notifyRead(String debug){
        byte[] buffer=mBuffer;
        synchronized (buffer){
            Debug.D(getClass(),"Notify buffer read "+(null!=debug?debug:"."));
            buffer.notify();
        }
    }

    final int read(byte[] buffer,int offset) {
        int size=null!=buffer?buffer.length:-1;
        int emptyLength=offset>=0&&size>0&&offset<size?size-offset:-1;
        if (emptyLength<0){
            return FATAL_ERROR;
        }
        if (emptyLength==0){
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
                    try {
                        Integer load=onLoadBytes(innerBuffer,cursor);//Try load bytes
                        if (null==load) {
                            Debug.D(getClass(), "Wait writing bytes while read is empty.");
                            innerBuffer.wait();
                            Debug.D(getClass(), "Read is wake up.");
                            continue;
                        }
                        if (load>0){
                            mCursor+=load;
                        }
                        return load;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return FATAL_ERROR;


                    }
                }
                int available=Math.min(emptyLength,cursor);//Not empty
                System.arraycopy(innerBuffer,0,buffer,offset,available);
//                StringBuffer st=new StringBuffer();
//                for (int i = offset; i < Math.min(100,available); i++) {
//                    st.append(Integer.toHexString(buffer[i]));
//                    st.append(" ");
//                }
//                Debug.D(getClass(),"AAA "+st);
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
