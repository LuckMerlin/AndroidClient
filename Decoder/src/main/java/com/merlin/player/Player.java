package com.merlin.player;

import java.lang.ref.WeakReference;

public class Player {
    private static WeakReference<OnDecodeFinishListener> mLintener;

    static {
        System.loadLibrary("linqiang");
    }

    public static void onDecodeFinish(byte[] bytes,int offset,int length){
        WeakReference<OnDecodeFinishListener> reference=mLintener;
        OnDecodeFinishListener listener=null!=reference?reference.get():null;
        if (null!=listener){
            listener.onDecodeFinish(bytes,offset,length);
        }
//        Log.d("LM","今天该 "+(null!=bytes?bytes.length:-1));

    }

    public native boolean play(String path,float seek);

    public native boolean playBytes(byte[] data,int offset,int length,boolean reset);

    public void setOnDecodeFinishListener(OnDecodeFinishListener listener){
        WeakReference<OnDecodeFinishListener> reference=mLintener;
        mLintener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=listener){
            mLintener=new WeakReference<>(listener);
        }
    }
}
