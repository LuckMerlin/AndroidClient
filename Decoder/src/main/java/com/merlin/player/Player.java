package com.merlin.player;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.merlin.debug.Debug;

import java.util.TreeMap;
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
    private final Buffer mBuffer=new Buffer(1024*1024);
    private PlayerRunnable mPlayerRunnable;
    private Playable mPlayable;
    private boolean mPaused=false;
    private long mSeek=0;

    static {
        System.loadLibrary("linqiang");
    }

    public final synchronized boolean run(){
        if (null!=mPlayerRunnable){
            return false;
        }
        final Buffer buffer=mBuffer;
        if (null==buffer){
            return false;
        }
        final PlayerRunnable cacheRunnable=new PlayerRunnable("Cache"){
            @Override
            protected void onRun() {
            }
        };
        final PlayerRunnable playerRunnable=mPlayerRunnable=new PlayerRunnable("Player"){
            @Override
            protected void onRun() {
                Thread thread=new Thread(cacheRunnable);
                thread.setDaemon(true);
                thread.start();
                create();
                cacheRunnable.quit();//Quit cache thread
                Runnable runnable=mPlayerRunnable;
                if (null!=runnable&&runnable==this){
                    mPlayerRunnable=null;
                }
            }};
        new Thread(playerRunnable).start();
        return true;
    }

    public final synchronized boolean release(){
        Runnable runnable=mPlayerRunnable;
        if (null==runnable){
            return false;
        }
        stop();
        mPlayerRunnable=null;
        synchronized (runnable){
            runnable.notify();
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
        Runnable runnable=mPlayerRunnable;
        if (null==runnable){
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
            synchronized (runnable) {
                runnable.notify();
            }
            return true;
        }
        Debug.W(getClass(),"Can't play media while media open fail.");
        return false;
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

    private int nativeLoadBytes(int offset,byte[] buffer){
        Runnable runnable=mPlayerRunnable;
        if (null==runnable){
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
            synchronized (runnable) {
                Debug.W(getClass(), "Wait media to play.");
                runnable.wait();
                Debug.W(getClass(), "Wake up.");
            }
            return NORMAL;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return NORMAL;
        }
    }

}
