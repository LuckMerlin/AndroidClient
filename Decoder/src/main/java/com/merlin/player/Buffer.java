package com.merlin.player;

import java.io.Closeable;
import java.io.IOException;

/*
Decode for jni
 */
public abstract class Buffer {
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

    private final Playable mPlayable;
    private final double mSeek;

    protected Buffer(Playable playable,double seek){
        mPlayable=playable;
        mSeek=seek;
    }

    public abstract boolean open(double seek,String debug);

    public abstract boolean close(String debug);

    public final Playable getPlayable() {
        return mPlayable;
    }

    protected abstract int read(byte[] buffer, int offset, int length);

    protected abstract boolean seek(double seek);

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
}
