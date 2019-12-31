package com.merlin.player;


import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;

public class Player {
    public final static int STATE_PLAYING=2001;
    public final static int STATE_PAUSE=2002;
    public final static int STATE_IDLE=2003;
    public final static int STATE_WAITING=2004;
    public final static int STATE_STOP=2005;

    private static WeakReference<OnMediaFrameDecodeFinish> mListener;
    private static WeakReference<OnStateUpdate> mUpdate;
    private PlayPending mPlayRunnable;
    private boolean mRunning=false;

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

    public void setOnStateUpdateListener(OnStateUpdate listener){
        WeakReference<OnStateUpdate> reference=mUpdate;
        mUpdate=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=listener){
            mUpdate=new WeakReference<>(listener);
        }
    }

    public synchronized boolean play(final String path,final float seek){
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't play media.path="+path);
            return false;
        }
        final PlayPending runnable=mPlayRunnable;
        if (null==runnable){
            mRunning=true;
            new Thread(mPlayRunnable=new PlayPending(new Pending(path,seek)) {
                @Override
                public void run() {
                    while (mRunning){
                       Pending task=pop();
                       if (null!=task) {
                           Player.this.playFile(task.mPath, task.mSeek);
                       }
                       Debug.D(Player.this.getClass(),"%%%%%%%%% ");
                    }
                    mPlayRunnable=null;
                    Debug.D(getClass(),"Recycling player resources.");
                }
            }).start();
            return true;
        }else{
            boolean putted=runnable.put(new Pending(path,seek));
            if (!isIdle()){
                Debug.D(getClass(),"Stop playing media before start new media."+path);
                pause(true);
            }
            return putted;
        }
    }

    public final boolean isIdle(){
        return STATE_IDLE==getPlayerState();
    }

    public final boolean isPlaying(String ...paths){
        if (STATE_PLAYING==getPlayerState()){
            return true;
        }
        return false;
    }

    public final boolean pause(){
        return pause(false);
    }

    public final boolean stop(){
        return pause(true);
    }

    public native int getPlayerState();

    public native long getPosition();

    public native long getDuration();

    public native long seek(float seek);

    public native boolean start(float seek);

    private native boolean pause(boolean stop);

    public final boolean isRunning(){
        return mRunning;
    }

    public boolean destory(){
        if (mRunning){
            mRunning=false;
            Debug.D(getClass(),"Destroy player.");
            return isIdle()||stop();
        }
        return false;
    }

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

    private final static void onStateChanged(int state){
        WeakReference<OnStateUpdate> reference=mUpdate;
        OnStateUpdate listener=null!=reference?reference.get():null;
        if (null!=listener){
            listener.onPlayerStateUpdated(state,null);
        }
    }

    private native boolean playBytes(String path,byte[] data,int length,long totalLength);

    private native boolean playFile(String path,float seek);

    private static class Pending{
        private final String mPath;
        private final float mSeek;
        private Pending(String path,float seek){
            mPath=path;
            mSeek=seek;
        }
    }

    private static abstract class PlayPending implements Runnable{
        private Pending mPending;
        private PlayPending(Pending pending){
            mPending=pending;
        }

        synchronized boolean put(Pending pending){
            if (null!=pending){
                mPending=pending;
                synchronized (this){
                    notify();
                }
                return true;
            }
            return false;
        }

         synchronized Pending pop() {
            Pending pending=mPending;
            if (null!=pending){
                mPending=null;
                return pending;
            }
             synchronized (this){
                 try {
                     Debug.D(getClass(),"Wait play task input.");
                     wait();
                     Debug.D(getClass(),"Wakeup for play task input.");
                 } catch (InterruptedException e) {
                     Debug.E(getClass(),"Can't wait task input.e="+e,e);
                 }
             }
            return null;
        }
    }
//    private native boolean playBytes(byte[] data,int offset,int length);

//    public native String getPlayingPath();

//    public native boolean seek(float seek);

//    public native long getDuration();

}
