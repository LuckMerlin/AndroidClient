package com.merlin.player;

public abstract class Player implements OnMediaFrameDecodeFinish{
    private static OnMediaFrameDecodeFinish mListener;

    static {
        System.loadLibrary("linqiang");
    }

    public Player(){
        mListener=this;
    }

    public native int play(Playable playable,double seek);

    /**
     *
     *Call by native C
     */
    private final static void onNativeDecodeFinish(int mediaType,byte[] bytes,int channels,int sampleRate,int speed,long currentPosition){
        OnMediaFrameDecodeFinish reference=mListener;
        if (null!=reference){
            reference.onMediaFrameDecodeFinish(mediaType,bytes,channels,sampleRate,speed,currentPosition);
        }
    }

}
