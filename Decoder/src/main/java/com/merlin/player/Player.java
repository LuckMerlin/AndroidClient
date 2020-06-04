package com.merlin.player;
import android.os.Handler;
import android.os.Looper;


import com.merlin.debug.Debug;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Player{
    private final static int  NORMAL = 0;
    private final static int  EOF = -1;
    public final static int  FATAL_ERROR = -2;
    public final static int  IDLE = -2003;
    public final static int  STOP =  -2005;
    public final static int  PLAYING =  -2007;
    public final static int  WAITING =  -2008;
    public final static int  PLAY =  -2009;
    public final static int  CREATE =  -2021;
    public final static int  DESTROY =  -2022;
    public final static int  ADD =  -2023;
    public final static int  REMOVE =  -2024;
    private boolean mWaiting=false;
    private final String mCacheFile;
    private RandomAccessFile mCacheAccess;
    private Playing mPlaying;
    private boolean mPaused=false;
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<OnPlayerStatusChange, Long> mChangeMap=new WeakHashMap<>();

    public interface OnPlayerStatusChange{
        void onPlayerStatusChanged(int status,Playable playable,Object arg,String debug);
    }

    static {
        System.loadLibrary("linqiang");
    }

    public Player(){
        this(null);
    }

    public Player(String cacheFile){
        mCacheFile=cacheFile;
    }

    public final boolean addListener(OnPlayerStatusChange change){
        Map<OnPlayerStatusChange, Long> changeMap=null!=change?mChangeMap:null;
        return null!=changeMap&&null!=changeMap.put(change,System.currentTimeMillis());
    }

    public final boolean removeListener(OnPlayerStatusChange change){
        Map<OnPlayerStatusChange, Long> changeMap=null!=change?mChangeMap:null;
        return null!=changeMap&&null!=changeMap.remove(change);
    }

    public final synchronized boolean run() {
        if (null!=mCacheAccess){
            return false;
        }
        notifyStatusChange(CREATE,null,null,null);
        String cachePath=mCacheFile;
        File cacheFile=null;
        if (null==cachePath||cachePath.length()<=0){
            try {
                cacheFile = File.createTempFile("player","cache");
            } catch (IOException e) {
                Debug.E(getClass(),"Can't run player while cache file create exception.e="+e,e);
                e.printStackTrace();
                notifyStatusChange(DESTROY,null,null,"Cache file create exception.");
                return false;
            }
        }else{
            cacheFile=new File(cachePath);
        }
        if (null==cacheFile){
            Debug.W(getClass(),"Can't run player while cache file NULL.");
            notifyStatusChange(DESTROY,null,null,"Cache file NULL.");
            return false;
        }
        final File finalCacheFile=cacheFile;
        try {
            finalCacheFile.deleteOnExit();
            final RandomAccessFile cacheAccess=new RandomAccessFile(finalCacheFile,"rw");
            final byte[] cacheBuffer=new byte[1024*1024];
            final OnLoadMedia innerLoader=(byte[] playerBuffer, int playerOffset) ->{
                final int playerBufferLength=null!=playerBuffer?playerBuffer.length:-1;
                if (playerBufferLength<=0||playerOffset<0){//Buffer or offset not invalid
                    Debug.W(getClass(),"Can't play media which player buffer or offset invalid.");
                    return FATAL_ERROR;
                }
                if (playerOffset>=playerBufferLength){//Already full
                    return NORMAL;
                }
                if (null==mCacheAccess){//Player has been stopped
                    Debug.W(getClass(),"Can't play media which cache access is NULL.");
                    return FATAL_ERROR;
                }
                final Playing playing=mPlaying;
                if (null==playing){//None media to play
                    wait(cacheAccess," While none media to play.");
                    return NORMAL;
                }
                Playable media=playing.mMedia;
                if (null==media){
                    Debug.W(getClass(),"Can't play media which is NULL."+playing);
                    stop("While media is NULL.");
                    return FATAL_ERROR;
                }
                if (!media.isOpened()&&!media.open()){
                    Debug.W(getClass(),"Can't play media which open fail."+playing);
                    stop("While media open fail.");
                    return FATAL_ERROR;
                }
                Meta meta=media.getMeta();
                long totalLength=null!=meta?meta.getLength():-1;
                if (totalLength<=0){
                    Debug.W(getClass(),"Can't play media which total length invalid."+totalLength);
                    stop("While media total length invalid."+totalLength);
                    return FATAL_ERROR;
                }
                if (media instanceof BytesMedia){
                    int read=((BytesMedia)media).read(playerBuffer,playerOffset);
                    if (read>0){
                        notifyStatusChange(PLAYING,media,null,null);
                    }
                    return read;
                }
                long length=cacheAccess.length();
                long loadCursor =playing.mLoadCursor;
                loadCursor=loadCursor<=0?0:loadCursor;
                if ((length<totalLength)&&(length<=0||loadCursor>length)){//Empty
                    final long currThreadId=Thread.currentThread().getId();
                    final Integer[] cacheLoad=new Integer[1];
                    if (!media.cache((inputStream)-> {
                        if (null==inputStream){
                            stop( "While cache media stream NULL.");
                            return;
                        }
                        if (currThreadId==Thread.currentThread().getId()){
                            stop( "While cache media stream from same thread with player thread.");
                            return;
                        }
                        long totalWritten=0;
                        try {
                            do {
                                int read=inputStream.read(cacheBuffer,0,cacheBuffer.length);
                                if (read<0){
                                    Debug.D(getClass(),"Cache media finish."+totalWritten+" "+cacheAccess.length());
                                    break;
                                }
                                if (read>0) {
                                    synchronized (cacheAccess) {
                                        cacheAccess.seek(cacheAccess.length());//Append to tail
                                        cacheAccess.write(cacheBuffer, 0, read);
                                    }
                                    totalWritten += read;
                                    long currCursor = playing.mLoadCursor;
                                    long currLength = cacheAccess.length();
                                    if (mWaiting && currLength > currCursor) {
                                        notify(cacheAccess, "After length more than cursor." + currCursor + " " + currLength);
                                    }
                                }
                            }while(true);
                        }catch (Exception  e){
                            Debug.E(getClass(),"Exception while read cache bytes.e="+e,e);
                            e.printStackTrace();
                            stop( "After read cache bytes exception.e="+e);
                        }
                    })){
                        Debug.W(getClass(),"Can't play media which cache fail."+playing);
                        stop("While media cache fail.");
                        return FATAL_ERROR;
                    }
                    Integer cached=cacheLoad[0];
                    if (null==cached) {//If already cached not need wait
                        wait(cacheAccess, " While buffer caching.");
                        return NORMAL;
                    }
                    return cached;
                }else if (loadCursor<length){//Need load
                    synchronized (cacheAccess) {
                        cacheAccess.seek(loadCursor);
                        int read = cacheAccess.read(playerBuffer, playerOffset, playerBufferLength - playerOffset);
                        if (read > 0) {
                            playing.mLoadCursor += read;
                            notifyStatusChange(PLAYING,media,null,null);
                        }
                        return read;
                    }
                }
                return NORMAL;
            };
            mCacheAccess=cacheAccess;
            new Thread(()->{
                    create((byte[] playerBuffer, int playerOffset)->{
                        int loaded=innerLoader.onLoadMedia(playerBuffer,playerOffset);
                        if (loaded==EOF){
                            Playable playable=getPlaying();
                            stop("While load media eof.");
                            onMediaPlayFinish(playable,"While load media eof.");
                        }
                        return loaded;
                    });
                    finalCacheFile.delete();
                    RandomAccessFile curr=mCacheAccess;
                    if (null!=curr&&curr==cacheAccess){
                        mCacheAccess=null;
                    }
                    notifyStatusChange(DESTROY,null,null,"Player thread end.");
            }).start();
            return true;
        } catch (FileNotFoundException e) {
            Debug.E(getClass(),"Can't run player while run exception.e="+e,e);
            e.printStackTrace();
            notifyStatusChange(DESTROY,null,null,"Exception player."+e);
            return false;
        }
    }

    protected void onMediaPlayFinish(Playable playable,String debug){
            //Do nothing
    }

    public final boolean isWaiting() {
        return mWaiting;
    }

    public final synchronized boolean release(){
        RandomAccessFile accessFile=mCacheAccess;
        if (null==accessFile){
            return false;
        }
        Debug.D(getClass(),"Release player.");
        mCacheAccess=null;
        stop("While release player.");
       notify(accessFile,"While release player.");
        return true;
    }

    public final boolean isPaused() {
        return mPaused;
    }

    public final boolean isIdle() {
        return null==mPlaying;
    }

    public final Playable getPlaying() {
        Playing playing=mPlaying;
        return null!=playing?playing.mMedia:null;
    }

    public final boolean pause(){
        if (!mPaused) {
            return mPaused = true;
        }
        return false;
    }

    public final boolean start(){
        if (mPaused){
            RandomAccessFile cacheAccess=mCacheAccess;
            if (null!=cacheAccess) {
                mPaused = false;
                synchronized (cacheAccess) {
                    cacheAccess.notify();
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean seek(double seek){
        Playing playing=mPlaying;
        if (null!=playing){
//            playing.
        }
        return false;
    }

    public final boolean stop(String debug) {
        Playing playing=mPlaying;
        if (null!=playing){
            mPlaying=null;
            cleanCached("While stop media "+(null!=debug?debug:"."));
            Playable playable=playing.mMedia;
            notifyStatusChange(STOP,playable,null,debug);
            return null!=playable&&playable.close();
        }
        return false;
    }

    public boolean play(Playable playable,double seek){
        if (playable==null){
            Debug.W(getClass(),"Can't play media while media is NULL.");
            return false;
        }
        final RandomAccessFile cacheAccess=mCacheAccess;
        if (null==cacheAccess){
            Debug.W(getClass(),"Can't play media while player not start.");
            return false;
        }
        if (!isRunning()){
            Debug.W(getClass(),"Can't play media while player not run.");
            return false;
        }
        stop("While play new media."+playable);
        mPlaying=new Playing(playable);
        notifyStatusChange(PLAY,playable,null,"While play new media.");
        notify(cacheAccess,"While play new media.");
        return true;
    }

    public final String getCachePath() {
        return mCacheFile;
    }

    public final boolean isRunning(){
        return null!=mCacheAccess;
    }

    protected void onStatusChanged(int status,Playable playable,Object arg,String debug){
        //Do nothing
    }

    protected final void notifyStatusChange(int status,Playable playable,Object arg,String debug,OnPlayerStatusChange change){
        if (null != change) {
            change.onPlayerStatusChanged(status, playable, arg,debug);
        }
    }

    protected final void notifyStatusChange(int status,Playable playable,Object arg,String debug){
       if (Thread.currentThread().getId()==Looper.getMainLooper().getThread().getId()) {
           onStatusChanged(status,playable,arg,debug);
           Map<OnPlayerStatusChange, Long> changeMap = mChangeMap;
           if (null!=changeMap) {
               synchronized (changeMap) {
                   Set<OnPlayerStatusChange> set = changeMap.keySet();
                   if (null != set) {
                       for (OnPlayerStatusChange change : set) {
                           notifyStatusChange(status, playable, arg, debug, change);
                       }
                   }
               }
           }
       }else{
           mHandler.post(()->notifyStatusChange(status,playable,arg,debug));
       }
    }

    private boolean cleanCached(String debug){
        RandomAccessFile accessFile=mCacheAccess;
        if (null!=accessFile){
            try {
                accessFile.setLength(0);
                Debug.D(getClass(),"Clean player cached "+(null!=debug?debug:"."));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    protected void onFrameDecoded(int mediaType,byte[] bytes,int channels,int sampleRate,int speed){
        //Do nothing
    }
    /**
     *
     *Call by native C
     */
    private void onMediaFrameDecodeFinish(int mediaType,byte[] bytes,int channels,int sampleRate,int speed){
        onFrameDecoded(mediaType,bytes,channels,sampleRate,speed);
    }

    private native boolean create(OnLoadMedia loader);

    private native boolean destroy();

    private final static class Playing{
        private long mLoadCursor;
        private final Playable mMedia;

        private Playing(Playable media){
            mMedia=media;
            mLoadCursor=0;
        }
    }

    private void notify(RandomAccessFile cache,String debug) {
        if (null!=cache) {
            synchronized (cache) {
                Debug.D(getClass(), "Notify "+(null!=debug?debug:"."));
                cache.notifyAll();
            }
        }
    }

    private void wait(RandomAccessFile cache,String debug) throws InterruptedException {
        if (null!=cache) {
            synchronized (cache) {
                Debug.D(getClass(), "Wait "+(null!=debug?debug:"."));
                mWaiting=true;
                notifyStatusChange(WAITING,null,null,"Cache file NULL.");
                cache.wait();
                mWaiting=false;
                Debug.D(getClass(), "Wake up after "+(null!=debug?debug:"."));
            }
        }
    }
}
