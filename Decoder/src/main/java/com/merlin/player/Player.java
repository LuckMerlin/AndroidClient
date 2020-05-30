package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.IOException;

public abstract class Player implements OnMediaFrameDecodeFinish{
    public final static int  NORMAL = 0;
    public final static int  IDLE = -2003;
    public final static int  STOP =  -2005;
    public final static int  PROGRESS =  -2006;
    public final static int  PLAYING =  -2007;
    public final static int  CREATE =  -2021;
    public final static int  DESTROY =  -2022;
    private Buffer mBuffer;
    private PlayerRunnable mPlayerRunnable;
    private Playable mPlayable;
    private boolean mPaused=false;

    static {
        System.loadLibrary("linqiang");
    }

    public final synchronized boolean run() {
        if (null!=mPlayerRunnable){
            return false;
        }
        final Buffer buffer=mBuffer;
        if (null==buffer){
            mBuffer=new Buffer(){
                @Override
                protected Integer onLoadBytes(byte[] buffer, int offset) throws IOException {
                    if (null==mPlayerRunnable){
                        return Buffer.FATAL_ERROR;
                    }
                    Playable playable=mPlayable;
                    if (null!=playable){
                        if (!playable.isOpened()&&!playable.open()){
                            Debug.W(getClass(),"Media open fail."+playable);
                            return Buffer.FATAL_ERROR;
                        }
                        int read=playable.read(buffer,offset);
                        switch (read){
                            case Buffer.EOF:
                                 Debug.D(getClass(),"Media play finish."+playable);
                                 mPlayable=null;
                                 playable.close();
                                 break;
                        }
                        return read;
                    }
                    return null;
                }
            };
        }
        final PlayerRunnable playerRunnable=mPlayerRunnable=new PlayerRunnable("Player"){
            @Override
            public void run() {
                Debug.D(getClass(),"SSSSSSSEEEEE Player begin");
                create();
                Runnable runnable=mPlayerRunnable;
                if (null!=runnable&&runnable==this){
                    mPlayerRunnable=null;
                }
                Debug.D(getClass(),"SSSSSSSEEEEE Player end");
            }
        };
        new Thread(playerRunnable).start();
        return true;
    }

    public final synchronized boolean release(){
        Runnable runnable=mPlayerRunnable;
        if (null==runnable){
            return false;
        }
        Debug.D(getClass(),"Release player.");
        mPlayerRunnable=null;
        stop();
        final Buffer buffer=mBuffer;
        mBuffer=null;
        if (null!=buffer) {
            buffer.notifyRead("While release player.");
        }
        return true;
    }

    public final boolean pauseStart(){
        boolean pause=mPaused;
        return pause?start():pause();
    }

    public final boolean isPaused() {
        return mPaused;
    }

    public final boolean pause(){
        if (!mPaused) {
            return mPaused = true;
        }
        return false;
    }

    public final boolean start(){
        if (mPaused){
            Runnable runnable=mPlayerRunnable;
            if (null!=runnable) {
                mPaused = false;
                synchronized (runnable) {
                    runnable.notify();
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean stop(){
        Playable playable=mPlayable;
        if (null!=playable){
            mPlayable=null;
            return playable.close();
        }
        return false;
    }

    public boolean play(Playable playable,double seek){
        if (playable==null){
            Debug.W(getClass(),"Can't play media while media is NULL.");
            return false;
        }
        final Buffer buffer=mBuffer;
        if (null==buffer){
            Debug.W(getClass(),"Can't play media while buffer is NULL.");
            return false;
        }
        if (!isRunning()){
            Debug.W(getClass(),"Can't play media while player not run.");
            return false;
        }
        final PlayerRunnable runnable=mPlayerRunnable;
        if (null==runnable){
            Debug.W(getClass(),"Can't play media while player not start.");
            return false;
        }
        final Playable curr=mPlayable;
        if (null!=curr&&curr.equals(playable)){
            Debug.W(getClass(),"Can't play media while already playing.");
            return false;
        }
        mPlayable=playable;
        synchronized (buffer){
            buffer.notifyRead("After play media changed.");
        }
        return true;
    }

    public final boolean isRunning(){
        return null!=mPlayerRunnable;
    }
    /**
     *
     *Call by native C
     */
    protected void onMediaFrameDecodeFinish(int mediaType,byte[] bytes,int channels,int sampleRate,int speed){
        //Do nothing
    }

    private native boolean create();

    private native boolean destroy();

    private int nativeLoadBytes(byte[] buffer,int offset){
        Runnable runnable=mPlayerRunnable;
        if (null==runnable){
            return Buffer.FATAL_ERROR;
        }
        final Buffer innerBuffer=mBuffer;
        if (null==innerBuffer){
            return Buffer.FATAL_ERROR;
        }
        return innerBuffer.read(buffer,offset);
    }
}
