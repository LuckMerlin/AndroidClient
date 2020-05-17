package com.merlin.player;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

public class BK_Player implements Status{
    private static OnMediaFrameDecodeFinish mListener;
    private static WeakHashMap<OnPlayerStatusUpdate,Long> mUpdate;
    private WeakReference<OnMediaFrameDecodeFinish> mDecodeListener;
    private long mCurrPosition;
    private static OnPlayerStatusUpdate mInnerUpdate;
    private MediaBuffer mPlaying;
    private Handler mHandler;
    private PlayPending mPlayRunnable;

    static {
        System.loadLibrary("linqiang");
    }

    public BK_Player(){
        mListener=this instanceof OnMediaFrameDecodeFinish?new OnMediaFrameDecodeFinish(){
            @Override
            public void onMediaFrameDecodeFinish(int mediaType, byte[] bytes, int channels, int sampleRate, int speed,long currentPosition) {
                BK_Player.this.mCurrPosition=currentPosition;
                ((OnMediaFrameDecodeFinish) BK_Player.this).onMediaFrameDecodeFinish(mediaType,bytes,channels,sampleRate,speed,currentPosition);
                WeakReference<OnMediaFrameDecodeFinish> reference=mDecodeListener;
                OnMediaFrameDecodeFinish listener=null!=reference?reference.get():null;
                if (null!=listener){
                    listener.onMediaFrameDecodeFinish(mediaType,bytes,channels,sampleRate,speed,currentPosition);
                }
            }
        }:null;
    }

    public final boolean setDecodeListener(OnMediaFrameDecodeFinish frameDecodeFinish){
        WeakReference<OnMediaFrameDecodeFinish> reference=mDecodeListener;
        mDecodeListener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=frameDecodeFinish){
            mDecodeListener=new WeakReference<>(frameDecodeFinish);
        }
        return true;
    }

    public final boolean addListener(OnPlayerStatusUpdate listener){
        WeakHashMap<OnPlayerStatusUpdate,Long> reference=null!=listener?(mUpdate=null!=mUpdate?mUpdate:new WeakHashMap<OnPlayerStatusUpdate, Long>()):null;
        if (null!=reference&&!reference.containsKey(listener)){
            reference.put(listener,System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public final boolean removeListener(OnPlayerStatusUpdate listener){
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

    public synchronized boolean play(final MediaBuffer buffer,final OnPlayerStatusUpdate update,String debug){
        if (null==buffer){
            Debug.W(getClass(),"Can't play media buffer.buffer="+buffer);
            notifyPlayStatus(STATUS_FINISH_ERROR,"Path invalid.",null);
            return false;
        }
        mInnerUpdate=null!=mInnerUpdate?mInnerUpdate:new OnPlayerStatusUpdate() {
            @Override
            public void onPlayerStatusUpdated(BK_Player p, final int status, final String note, final IPlayable media, final Object data) {
                if (status==STATUS_IDLE||status==STATUS_STOP){
                    mCurrPosition=0;
                }
                if (BK_Player.this instanceof OnPlayerStatusUpdate){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((OnPlayerStatusUpdate) BK_Player.this).onPlayerStatusUpdated(BK_Player.this,status,note,media,data);
                        }
                    });
                 }
                if (null!=update){
                    update.onPlayerStatusUpdated(BK_Player.this,status,note,media,data);
                }
                final WeakHashMap<OnPlayerStatusUpdate,Long> reference=mUpdate;
                if (null!=reference){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (reference){
                                Set<OnPlayerStatusUpdate> set=reference.keySet();
                                if (null!=set){
                                    for (OnPlayerStatusUpdate update:set) {
                                        if (null!=update){
                                            update.onPlayerStatusUpdated(BK_Player.this,status,note,media,data);
                                        }
                                    }
                                }
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
                           Debug.D(getClass(),"Play "+task);
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

    public final boolean isPlaying(){
        return STATUS_PLAYING==getPlayerStatus();
    }

    public final boolean isPaused(){
        return STATUS_PAUSE==getPlayerStatus();
    }

    public final boolean isRunning(){
        PlayPending pending=mPlayRunnable;
        return null!=pending&&pending.mRunning;
    }

    public long getDuration(){
        return 0;
    }

    public float getCurrentProgress(){
        return 0;
    }
//    public long getCurrentDuration(boolean nat){
//        return nat?getPosition():mCurrPosition;
//    }

    public final IPlayable getPlaying(){
        MediaBuffer buffer=getPlayingBuffer();
        return null!=buffer?buffer.getPlayable():null;
    }

    public final boolean seek(double seek){
        MediaBuffer buffer=getPlayingBuffer();
        return null!=buffer&&buffer.seek(seek);
    }

    protected final MediaBuffer getPlayingBuffer(){
        return mPlaying;
    }

    protected final static void notifyPlayStatus(int status, String note, IPlayable media){
        OnPlayerStatusUpdate update=mInnerUpdate;
        if (null!=update){
            update.onPlayerStatusUpdated(null,status,note,media,null);
        }
    }

    protected final boolean runOnUiThread(Runnable runnable){
        if (null!=runnable){
            Handler handler=mHandler;
            handler=null==handler?(mHandler=new Handler(Looper.getMainLooper())):handler;
            return handler.post(runnable);
        }
        return false;
    }

    public boolean destroy(){
        PlayPending pending=mPlayRunnable;
        if (null!=pending){
            pending.mRunning=false;
        }
        Debug.D(getClass(),"Destroy player.");
        boolean succeed=isIdle()||pause(true);
        WeakHashMap reference=mUpdate;
        mCurrPosition=0;
        mUpdate=null;
        mHandler=null;
        mListener=null;
        if (null!=reference){
            reference.clear();
        }
        mInnerUpdate=null;
        return succeed;
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
    private final static void onNativeDecodeFinish(int mediaType,byte[] bytes,int channels,int sampleRate,int speed,long currentPosition){
        OnMediaFrameDecodeFinish reference=mListener;
        if (null!=reference){
            reference.onMediaFrameDecodeFinish(mediaType,bytes,channels,sampleRate,speed,currentPosition);
        }
    }

    private final static void onStatusChanged(int status,MediaBuffer buffer,String note){
        notifyPlayStatus(status,note,null!=buffer?buffer.getPlayable():null);
    }

    public native int getPlayerStatus();

    private native int playMedia(MediaBuffer buffer, double seek);

//    private native long getPosition();

//    private native long getDuration();

    public native boolean start(double seek);

    public native boolean pause(boolean stop);
}
