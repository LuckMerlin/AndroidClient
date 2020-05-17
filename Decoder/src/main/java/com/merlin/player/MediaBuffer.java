package com.merlin.player;

import java.io.Closeable;
import java.io.IOException;

/*
Decode for jni
 */
public abstract class MediaBuffer<T extends IPlayable> {
    public final static int READ_FINISH_SUCCEED = -1220;
    public final static int READ_FINISH_OUT_BOUNDS = -1221;
    public final static int READ_FINISH_TERMINAL = -1222;
    public final static int READ_FINISH_STOP = -1223;
    public final static int READ_FINISH_ARG_INVALID = -1224;
    public final static int READ_FINISH_NOT_OPEN = -1225;
    public final static int READ_FINISH_EXCEPTION = -1226;
    public final static int BUFFER_READ_FINISH_EOF =-1;
    public final static int BUFFER_READ_FINISH_INNER_ERROR = -5;
    public final static int BUFFER_READ_FINISH_NORMAL = -2;
    public final static int BUFFER_READ_FINISH_EXCEPTION = -6;
//    public final static int BUFFER_READ_FINISH_EOFE = -7 ;

    private final T mPlayable;
    private final double mSeek;

    protected MediaBuffer(T playable, double seek){
        mPlayable=playable;
        mSeek=seek;
    }

    protected abstract boolean open(double seek,String debug);

    protected abstract boolean close(String debug);

    public final T getPlayable() {
        return mPlayable;
    }

    protected abstract int read(byte[] buffer, int offset, int length);

    protected abstract boolean seek(double seek);

    public Long getContentLength(){
        return null;
    }

    public final double getSeek() {
        return mSeek;
    }

    public abstract boolean isOpened();

    protected final boolean closeIO(Closeable closeable){
        if (null!=closeable){
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
               //Do nothing
            }
        }
        return false;
    }

    @Override
    public String toString() {
        T playable=mPlayable;
        return ""+playable+" "+super.toString();
    }
}
