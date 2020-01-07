package com.merlin.player;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

public class Player implements Status{
    private static WeakReference<OnMediaFrameDecodeFinish> mListener;
    private static WeakHashMap<OnPlayerStatusUpdate,Long> mUpdate;
    private static OnPlayerStatusUpdate mInnerUpdate;
    private MediaBuffer mPlaying;
    private Handler mHandler;
    private PlayPending mPlayRunnable;

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

    public boolean addListener(OnPlayerStatusUpdate listener){
        WeakHashMap<OnPlayerStatusUpdate,Long> reference=null!=listener?(mUpdate=null!=mUpdate?mUpdate:new WeakHashMap<OnPlayerStatusUpdate, Long>()):null;
        if (null!=reference&&!reference.containsKey(listener)){
            reference.put(listener,System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public boolean removeListener(OnPlayerStatusUpdate listener){
        WeakHashMap<OnPlayerStatusUpdate,Long> reference=null!=listener?mUpdate:null;
        return null!=reference&&null!=reference.remove(listener);
    }

    public boolean togglePausePlay(Object media){
        if (!isIdle()){
            return isPlaying()?pause(false):start(-1);
        }
        return false;
    }

    protected MediaBuffer onResolveNext(MediaBuffer buffer){
        //Do nothing
        return null;
    }

    public synchronized boolean play(final MediaBuffer buffer, OnPlayerStatusUpdate update){
        if (null==buffer){
            Debug.W(getClass(),"Can't play media buffer.buffer="+buffer);
            notifyPlayStatus(STATUS_FINISH_ERROR,"Path invalid.",buffer,null);
            return false;
        }
        mInnerUpdate=null!=mInnerUpdate?mInnerUpdate:new OnPlayerStatusUpdate() {
            @Override
            public void onPlayerStatusUpdated(Player p,final int status,final String note,final Object media,final Object data) {
                final WeakHashMap<OnPlayerStatusUpdate,Long> reference=mUpdate;
                if (null!=reference){
                    Handler handler=mHandler=null!=mHandler?mHandler:new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (reference){
                                Set<OnPlayerStatusUpdate> set=reference.keySet();
                                if (null!=set){
                                    for (OnPlayerStatusUpdate update:set) {
                                        if (null!=update){
                                            update.onPlayerStatusUpdated(Player.this,status,note,media,data);
                                        }
                                    }
                                }
                            }
                            if (Player.this instanceof OnPlayerStatusUpdate){
                                ((OnPlayerStatusUpdate)Player.this).onPlayerStatusUpdated(Player.this,status,note,media,data);
                            }
                        }
                    });
                }
            }
        };
        final PlayPending runnable=mPlayRunnable;
        if (null==runnable){
            new Thread(mPlayRunnable=new PlayPending(buffer) {
                @Override
                public void run() {
                    mRunning=true;
                    MediaBuffer lastPlay=null;
                    while (mRunning){
                       final MediaBuffer task=pop(lastPlay);
                       if (null!=task){
                           lastPlay=mPlaying;
                           mPlaying=task;
                           playMedia(task,task.getSeek());
                           mPlaying=null;
                       }
                    }
                    lastPlay=null;
                    mPlayRunnable=null;
                    mHandler=null;
                    Debug.D(getClass(),"Recycling player resources.");
                }
            }).start();
            return true;
        }else{
            boolean putted=runnable.put(buffer);
            if (!isIdle()){
                Debug.D(getClass(),"Stop playing media before start new media."+buffer);
                pause(true);
            }
            return putted;
        }
    }

    public final MediaBuffer getNext(){
        PlayPending runnable=mPlayRunnable;
        MediaBuffer pending=null!=runnable?runnable.mPending:null;
        return pending;
    }

    public final boolean setNext(MediaBuffer buffer, String debug){
        if (null!=buffer){
            PlayPending runnable=mPlayRunnable;
            if (null==runnable){
                Debug.W(getClass(),"Can't set media next While player not running "+(null!=debug?debug:"."));
                return false;
            }
            return runnable.put(buffer);
        }
        return false;
    }

    public final boolean isIdle(){
        return STATUS_IDLE==getPlayerStatus();
    }


    public Playable getPlaying(Object ...objects){
        Playable playing=null;
        return playing;
    }

    public final boolean isPlaying(Object ...media){
        if (STATUS_PLAYING==getPlayerStatus()){
            return true;
        }
        return false;
    }

    public final boolean isPaused(Object ...paths){
        if (STATUS_PAUSE==getPlayerStatus()){
            return true;
        }
        return false;
    }

    public final boolean pausePlay(boolean stop,Object ...objects){
        if (null!=objects&&objects.length>0){
            Playable playing=getPlaying();
            if (null!=playing){
                Debug.D(getClass(),"$$$$$$$$$$ "+playing);
            }
            return false;
        }
        return pause(stop);
    }

    public final boolean isRunning(){
        PlayPending pending=mPlayRunnable;
        return null!=pending&&pending.mRunning;
    }

    public final Playable getPlaying(){
        MediaBuffer buffer=getPlayingBuffer();
        return null!=buffer?buffer.getPlayable():null;
    }


    private MediaBuffer getPlayingBuffer(){
        return mPlaying;
    }

    public boolean destroy(){
            PlayPending pending=mPlayRunnable;
            if (null!=pending){
                pending.mRunning=false;
            }
            Debug.D(getClass(),"Destroy player.");
            boolean succeed=isIdle()||pause(true);
            WeakHashMap reference=mUpdate;
            mUpdate=null;
            if (null!=reference){
                reference.clear();
            }
            mInnerUpdate=null;
            return succeed;
    }

    protected final static void notifyPlayStatus(int status,String note,Object media,Object data){
        OnPlayerStatusUpdate update=mInnerUpdate;
        if (null!=update){
            update.onPlayerStatusUpdated(null,status,note,media,data);
        }
    }

    private abstract class PlayPending implements Runnable{
        boolean mRunning=false;
        private MediaBuffer mPending;

        private PlayPending(MediaBuffer pending){
            mPending=pending;
        }

        synchronized boolean put(MediaBuffer pending){
            if (null!=pending){
                mPending=pending;
                synchronized (this){
                    notify();
                }
                return true;
            }
            return false;
        }

         synchronized MediaBuffer pop(MediaBuffer lastPlay) {
             MediaBuffer pending=mPending;
            if (null!=pending){
                mPending=null;
                return pending;
            }
            MediaBuffer next=onResolveNext(lastPlay);
            if (null!=next){
                return next;
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

    private final static void notifyStatusChanged(int status,String note,String path){
        notifyPlayStatus(status,note,path,null);
    }

    private native boolean playFile(String path,float seek);

    public native int getPlayerStatus();

    private native boolean playMedia(MediaBuffer buffer, double seek);

    public native long getPosition();

    public native long getDuration();

    public native boolean seek(double seek);

    public native boolean start(float seek);

    private native boolean pause(boolean stop);
}
