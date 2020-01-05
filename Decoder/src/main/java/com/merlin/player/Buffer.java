package com.merlin.player;

import java.io.Closeable;
import java.io.IOException;

/*
Decode for jni
 */
public abstract class Buffer {
    public final static  int STEP_FINISH_OPEN_FAILED=-1120;
    public final static  int STEP_FINISH_SUCCEED=-1123;
    public final static  int STEP_FINISH_ALREADY=-1124;
    public final static int READ_FINISH_SUCCEED = -1220;
    public final static int READ_FINISH_OUT_BOUNDS = -1221;
    public final static int READ_FINISH_TERMINAL = -1222;
    public final static int READ_FINISH_STOP = -1223;
    public final static int READ_FINISH_ARG_INVALID = -1224;
    public final static int READ_FINISH_NOT_OPEN = -1225;
    public final static int READ_FINISH_EXCEPTION = -1226;
    private final Playable mplayable;

    protected Buffer(Playable playable){
        mplayable=playable;
    }

    private long mLength=-1;
    public interface OpenStep{
        void onOpenStepFinish(boolean succeed,boolean fix,int what,long length);
    }

    public interface CloseStep{
        void onCloseStepFinish(boolean succeed,int what);
    }

//    protected abstract boolean onOpen(OpenStep step,long seek,String debug);

    public abstract boolean open(String debug);

    protected abstract boolean onClose(CloseStep step,String debug);

    final boolean close(final CloseStep step,String debug){
        return onClose(new CloseStep() {
            @Override
            public void onCloseStepFinish(boolean succeed, int what) {
                if (succeed){
                    mLength=-1;
                }
            }
        },debug);
    }

    public final long getLength(){
        return mLength;
    }

    protected abstract int read(byte[] buffer,double seek);

    public boolean isOpened(){
        return mLength>=0;
    }

    void finishOpenStep(boolean succeed,boolean fix,int what,long length,OpenStep step,String note){
        if (null!=step){
            step.onOpenStepFinish(succeed,fix,what,length);
        }
    }

    void finishCloseStep(boolean succeed,int what,CloseStep step,String note){
        if (null!=step){
            step.onCloseStepFinish(succeed,what);
        }
    }

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
