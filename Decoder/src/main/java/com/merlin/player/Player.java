package com.merlin.player;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

public class Player implements Status{
    private static WeakReference<OnMediaFrameDecodeFinish> mListener;
    private static WeakHashMap<OnPlayerStatusUpdate,Long> mUpdate;
    private static OnPlayerStatusUpdate mInnerUpdate;
    private Handler mHandler;
    private PlayPending mPlayRunnable;
    private Buffer mPlaying=null;

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

    public Object getPlaying(Object ...objects){
        Object playing=mPlaying;
        if (null!=objects&&objects.length>0&&null!=playing){
            Object object=playing instanceof Playable?((Playable)playing).getPath():null;
            String path=null!=object&&object instanceof String?(String)object:null;
            for (Object obj:objects) {
                if (null!=obj){
                    if (obj instanceof String&&null!=path&&path.equals(obj)){
                        return playing;
                    }else if (obj instanceof Playable&&null!=playing&&playing instanceof Playable&&playing.equals(obj)){
                         return (playing);
                    }
                }
            }
        }
        return playing;
    }

    public synchronized boolean play(Playable media,float seek){
        return play(media,seek,null);
    }

    public synchronized boolean play(final Playable playable,final float seek,OnPlayerStatusUpdate update){
        final String path=null!=playable ?playable.getPath():null;
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
        if (null==path||path.length()<=0){
            Debug.W(getClass(),"Can't play media.path="+path);
            notifyPlayStatus(STATUS_FINISH_ERROR,"Path invalid.",playable,seek);
            return false;
        }
        final PlayPending runnable=mPlayRunnable;
        if (null==runnable){
            new Thread(mPlayRunnable=new PlayPending(new Pending(playable,seek)) {
                @Override
                public void run() {
                    mRunning=true;
                    while (mRunning){
                       final Pending task=pop();
                        Playable media=null!=task?task.mMedia:null;
                        //Try play local file
                        String path=null!=media?media.getPath():null;
                        File file=null!=path&&path.length()>0?new File(path):null;
                       if (null!=file&&file.length()>0) {
                           mPlaying=new FileBuffer(media);
                           play(mPlaying,seek);
                           if (null!=path&&path.length()>0){

                           }else{
                               notifyPlayStatus(STATUS_FINISH_ERROR,"Path invalid.",playable,path);
                           }
                       }
                        mPlaying=null;
                    }
                    mPlayRunnable=null;
                    Debug.D(getClass(),"Recycling player resources.");
                }
            }).start();
            return true;
        }else{
            boolean putted=runnable.put(new Pending(playable,seek));
            if (!isIdle()){
                Debug.D(getClass(),"Stop playing media before start new media."+path);
                pause(true);
            }
            return putted;
        }
    }

    public final boolean isIdle(){
        return STATUS_IDLE==getPlayerStatus();
    }

    public final boolean isPlaying(Object ...paths){
        if (STATUS_PLAYING==getPlayerStatus()){
            return true;
        }
        return false;
    }

    public final boolean pausePlay(boolean stop){
        Object playing=mPlaying;
        if (null!=playing){
            Debug.D(getClass(),"$$$$$$$$$$ "+playing);
//            if (playing instanceof CachingMedia){
//                MediaBuffer.Canceler canceler=((CachingMedia)playing).mCanceler;
//                Debug.D(getClass(),"$$$$$$$dd $$$ "+canceler);
//                if (null!=canceler){
//                    canceler.cancel(true);
//                }
//            }
        }
        return pause(stop);
    }

    public final Object getPlaying() {
        return mPlaying;
    }

    public native int getPlayerStatus();

    public native String getPlayingPath();

    private native boolean play(Buffer buffer,double seek);

    public native long getPosition();

    public native long getDuration();

    public native long seek(float seek);

    public native boolean start(float seek);

    private native boolean pause(boolean stop);

    public final boolean isRunning(){
        PlayPending pending=mPlayRunnable;
        return null!=pending&&pending.mRunning;
    }

    public boolean destroy(){
            PlayPending pending=mPlayRunnable;
            if (null!=pending){
                pending.mRunning=false;
                mPlaying=null;
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

    protected final static void notifyPlayStatus(int status,String note,Object media,Object data){
        OnPlayerStatusUpdate update=mInnerUpdate;
        if (null!=update){
            update.onPlayerStatusUpdated(null,status,note,media,data);
        }
    }

    private native boolean playFile(String path,float seek);

    private static class Pending{
        private final Playable mMedia;
        private final float mSeek;
        private Pending(Playable path,float seek){
            mMedia=path;
            mSeek=seek;
        }
    }

    private static abstract class PlayPending implements Runnable{
        boolean mRunning=false;
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

//    protected static class PendingMedia{
//        private final Playable mMedia;
//        private PendingMedia(Playable media){
//            mMedia=media;
//        }
//    }

//    protected static class CachePending{
//        private final Playable mMedia;
//
//        public CachePending(Playable media, MediaBuffer.Canceler canceler){
//            mMedia=media;
////            mCanceler=canceler;
//        }
//
//        public final Playable getMedia() {
//            return mMedia;
//        }
//    }
}
