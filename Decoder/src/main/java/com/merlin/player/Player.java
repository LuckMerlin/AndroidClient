package com.merlin.player;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

public class Player {
    private static WeakReference<OnMediaFrameDecodeFinish> mListener;
    private Runnable mPlayRunnable;

    static {
        System.loadLibrary("linqiang");
    }

    public void setOnDecodeFinishListener(OnMediaFrameDecodeFinish listener){
        WeakReference<OnMediaFrameDecodeFinish> reference=mListener;
        mListener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=listener){
            mListener=new WeakReference<>(listener);
        }
    }

    public boolean play(final String path,final float seek){
        ExecutorService pool=mPool;
        return null!=path&&path.length()>0&&null!=pool&&null!=pool.submit(new Runnable() {
            @Override
            public void run() {
                playFile(path,seek);
            }
        });
    }

    public native int getPlayerState();

    public native long getPosition();

    public native long getDuration();

    public native long seek(float seek);

    public native boolean start(float seek);

    public native boolean pause(boolean stop);

    /**
     *
     *Call by native C
     */
    private final static void onNativeDecodeFinish(int mediaType,byte[] bytes,int offset,int length,int speed){
        WeakReference<OnMediaFrameDecodeFinish> reference=mListener;
        OnMediaFrameDecodeFinish listener=null!=reference?reference.get():null;
        if (null!=listener){
            listener.onMediaFrameDecodeFinish(mediaType,bytes,offset,length);
        }
    }

    private native boolean playBytes(String path,byte[] data,int length,long totalLength);

    private native boolean playFile(String path,float seek);

//    private native boolean playBytes(byte[] data,int offset,int length);

//    public native String getPlayingPath();

//    public native boolean seek(float seek);

//    public native long getDuration();

}
