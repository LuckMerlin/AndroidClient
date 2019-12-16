package com.merlin.player;

import java.lang.ref.WeakReference;

public class Player {
    private static WeakReference<OnDecodeFinish> mLintener;

    static {
        System.loadLibrary("linqiang");
    }

    public void setOnDecodeFinishListener(OnDecodeFinish listener){
        WeakReference<OnDecodeFinish> reference=mLintener;
        mLintener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=listener){
            mLintener=new WeakReference<>(listener);
        }
    }

    public static void onDecodeFinish(byte[] bytes,int offset,int length){
        WeakReference<OnDecodeFinish> reference=mLintener;
        OnDecodeFinish listener=null!=reference?reference.get():null;
        if (null!=listener){
            listener.onDecodeFinish(bytes,offset,length);
        }
    }

    public native boolean play(String path,float seek);

    public native boolean playBytes(byte[] data,int offset,int length,boolean reset);

//    public native boolean seek(float seek);

//    public native long getDuration();

}
