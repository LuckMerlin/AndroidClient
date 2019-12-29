package com.merlin.player;

import java.lang.ref.WeakReference;

public class Player {
    private static WeakReference<OnMediaFrameDecodeFinish> mLintener;

    static {
        System.loadLibrary("linqiang");
    }

    public void setOnDecodeFinishListener(OnMediaFrameDecodeFinish listener){
        WeakReference<OnMediaFrameDecodeFinish> reference=mLintener;
        mLintener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=listener){
            mLintener=new WeakReference<>(listener);
        }
    }

    /**
     *
     *Call by native C
     */
    private final static void onNativeDecodeFinish(int mediaType,byte[] bytes,int offset,int length,int speed){
        WeakReference<OnMediaFrameDecodeFinish> reference=mLintener;
        OnMediaFrameDecodeFinish listener=null!=reference?reference.get():null;
        if (null!=listener){
            listener.onMediaFrameDecodeFinish(mediaType,bytes,offset,length);
        }
    }

    public native boolean play(String path,float seek);

    public native boolean playBytes(byte[] data,int offset,int length);

//    public native boolean seek(float seek);

//    public native long getDuration();

}
