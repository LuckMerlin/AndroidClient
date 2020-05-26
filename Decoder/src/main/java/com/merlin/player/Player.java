package com.merlin.player;

import com.merlin.debug.Debug;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class Player implements OnMediaFrameDecodeFinish{
    public final static int  NORMAL = 0;
    public final static int  END = -1;
    public final static int  IDLE = -2003;
    public final static int  STOP =  -2005;
    public final static int  PROGRESS =  -2006;
    public final static int  PLAYING =  -2007;
    public final static int  CREATE =  -2021;
    public final static int  DESTROY =  -2022;
    public final static int  FATAL_ERROR =  -2023;
    private final ExecutorService mExecutor=Executors.newFixedThreadPool(2);
    private Future mFuture;
    private Playable mPlayable;
    private boolean mPaused=false;
    private long mSeek=0;

    static {
        System.loadLibrary("linqiang");
    }

    public final synchronized boolean run(){
        Future future=mFuture;
        if (null!=future){
            return false;
        }
        mFuture=mExecutor.submit(new PlayRunnable() {
            @Override
            public void run() {
                create();
                mFuture=null;
            }
        });
        return true;
    }

    public final synchronized boolean release(){
        Future future=mFuture;
        if (null==future){
            return false;
        }
        mSeek=0;
        synchronized (future){
            future.notify();
            future.cancel(true);
            return true;
        }
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
            Future future=mFuture;
            if (null!=future) {
                mPaused = false;
                synchronized (future) {
                    future.notify();
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean stop(){
        Playable playable=mPlayable;
        if (null!=playable){
            mSeek=0;
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
        if (!isRunning()){
            Debug.W(getClass(),"Can't play media while player not run.");
            return false;
        }
        final Future future=mFuture;
        if (null==future){
            Debug.W(getClass(),"Can't play media while player not start.");
            return false;
        }
        final Playable curr=mPlayable;
        playable=(null==curr||!curr.equals(playable))?mPlayable=playable:curr;
        if (playable.isOpened()||playable.open()){
//            Meta meta=playable.getMeta();
//            long length=null!=meta?meta.getLength():-1;
//            if (length<=0){
//                Debug.W(getClass(),"Can't play media while media length invalid.");
//                return stop()&&false;
//            }
            synchronized (future) {
                future.notify();
            }
            return true;
        }
        Debug.W(getClass(),"Can't play media while media open fail.");
        return false;
    }

    public final boolean isRunning(){
        return null!=mFuture;
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

    private int nativeLoadBytes(int offset,byte[] buffer){
        final Future future=mFuture;
        if (null==future){
            return DESTROY;
        }
        final Playable playable=mPlayable;
        if (null!=playable&&playable.isOpened()){
            long seek=mSeek;
            int read=playable.read(seek<=0?0:seek,offset,buffer);
            if (read<0){
                return END;
            }
            mSeek=0;
            return read;
        }
        try {
            synchronized (future) {
                Debug.W(getClass(), "Wait media to play.");
                future.wait();
//                Debug.W(getClass(), "Wake up.");
            }
            return NORMAL;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return NORMAL;
        }
    }

}
