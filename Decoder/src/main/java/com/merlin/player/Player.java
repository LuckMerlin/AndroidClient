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

public abstract class Player implements Status{
    private final static int  NORMAL = 0;
    private final static int  EOF = -1;
    private boolean mWaiting=false;
    private final String mCacheFile;
    private RandomAccessFile mCacheAccess;
    private Playing mPlaying;
    private boolean mPaused=false;
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<OnPlayerStatusChange, Long> mChangeMap=new WeakHashMap<>();

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

    public final synchronized boolean run(String debug) {
        if (null!=mCacheAccess){
            return false;
        }
        notifyStatusChange(CREATE,getPlaying(null),null,null);
        String cachePath=mCacheFile;
        File cacheFile=null;
        if (null==cachePath||cachePath.length()<=0){
            try {
                cacheFile = File.createTempFile("player","cache");
            } catch (IOException e) {
                Debug.E(getClass(),"Can't run player while cache file create exception.e="+e,e);
                e.printStackTrace();
                notifyStatusChange(DESTROY,getPlaying(null),null,"Cache file create exception.");
                return false;
            }
        }else{
            cacheFile=new File(cachePath);
        }
        if (null==cacheFile){
            Debug.W(getClass(),"Can't run player while cache file NULL.");
            notifyStatusChange(DESTROY,getPlaying(null),null,"Cache file NULL.");
            return false;
        }
        final File finalCacheFile=cacheFile;
        try {
            finalCacheFile.deleteOnExit();
            final RandomAccessFile cacheAccess=new RandomAccessFile(finalCacheFile,"rw");
            final byte[] cacheBuffer=new byte[1024*1024];
            final OnLoadMedia innerLoader=(byte[] playerBuffer, int playerOffset) ->{
                while (mPaused){
                    wait(cacheAccess,"While pause flag setted.");
                }
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
                Playable media=playing.getMedia();
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
                long cursor = playing.getCursor();
                final long loadCursor=cursor<=0?0:cursor;
                if (media instanceof BytesMedia){
                    int read=((BytesMedia)media).read(playerBuffer,playerOffset,loadCursor);
                    if (read>0){
                        playing.increaseCursor(read);
                        notifyStatusChange(PLAYING,media,null,null);
                    }
                    return read;
                }
                long length=cacheAccess.length();
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
                                    playing.setCacheOver(true);
                                    Debug.D(getClass(),"Cache media finish."+totalWritten+" "+cacheAccess.length());
                                    break;
                                }
                                if (read>0) {
                                    synchronized (cacheAccess) {
                                        cacheAccess.seek(cacheAccess.length());//Append to tail
                                        cacheAccess.write(cacheBuffer, 0, read);
                                    }
                                    totalWritten += read;
                                    long currCursor = playing.getCursor();
                                    long currLength = cacheAccess.length();
                                    if (currLength > currCursor) {
                                        if (mWaiting) {//Must keep alone for this if logical
                                            notify(cacheAccess, "After length more than cursor." + currCursor + " " + currLength);
                                        }
                                    }
                                }
                            }while(true);
                        }catch (Exception  e){
                            Debug.E(getClass(),"Exception while read cache bytes.e="+e,e);
                            e.printStackTrace();
                            stop( null,"After read cache bytes exception.e="+e);
                        }
                    })){
                        Debug.W(getClass(),"Can't play media which cache fail."+playing);
                        stop(null,"While media cache fail.");
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
                            playing.increaseCursor(read);
                            notifyStatusChange(PLAYING,media,null,null);
                        }
                        return read;
                    }
                }

                return loadCursor>=length&&playing.isCacheOver()?EOF:NORMAL;
            };
            mCacheAccess=cacheAccess;
            new Thread(()->{
                    create((byte[] playerBuffer, int playerOffset)->{
                        int loaded=innerLoader.onLoadMedia(playerBuffer,playerOffset);
                        if (loaded==EOF){
                            Playable playable=getPlaying(null,false);
                            stop(null,"While load media eof.");
                            onMediaPlayFinish(playable,"While load media eof.");
                        }
                        return loaded;
                    });
                    finalCacheFile.delete();
                    RandomAccessFile curr=mCacheAccess;
                    if (null!=curr&&curr==cacheAccess){
                        mCacheAccess=null;
                    }
                    notifyStatusChange(DESTROY,getPlaying(null),null,"Player thread end.");
            }).start();
            return true;
        } catch (FileNotFoundException e) {
            Debug.E(getClass(),"Can't run player while run exception.e="+e,e);
            e.printStackTrace();
            notifyStatusChange(DESTROY,getPlaying(null),null,"Exception player."+e);
            return false;
        }
    }

    protected void onMediaPlayFinish(Playable playable,String debug){
            //Do nothing
    }

    public final boolean isWaiting() {
        return mWaiting;
    }

    public final synchronized boolean release(String debug){
        RandomAccessFile accessFile=mCacheAccess;
        if (null==accessFile){
            return false;
        }
        Debug.D(getClass(),"Release player "+(null!=debug?debug:"."));
        mCacheAccess=null;
        stop(null,"While release player.");
        notify(accessFile,"While release player.");
        return true;
    }

    public final boolean isPaused() {
        return mPaused;
    }

    public final boolean isIdle() {
        return null==mPlaying;
    }

    public final Playable getPlaying(Object object) {
        return getPlaying(object,null);
    }

    public final Playable getPlaying(Object object,Boolean justPlaying) {
        Playing playing=mPlaying;
        Playable media=null!=playing?playing.getMedia():null;
        return null!=media&&(null==justPlaying||!justPlaying||!isPaused())&&(null==object||
                media.equals(object))?media:null;
    }

    public final boolean isPlaying(Object object){
        return isPlaying(object,null);
    }

    public final boolean isPlaying(Object object,Boolean justPlaying){
        return null!=getPlaying(object,justPlaying);
    }

    public final boolean pause(Object arg,String debug){
        if (null==arg||isPlaying(arg)){
            if (!mPaused) {
                Debug.D(getClass(),"Pause play media "+(null!=debug?debug:"."));
                mPaused = true;
                notifyStatusChange(PAUSE,getPlaying(null),arg,debug);
                return true;
            }
        }
        return false;
    }

    public final boolean start(Object obj,String debug){
        if ((null==obj||isPlaying(obj))&&mPaused){
            RandomAccessFile cacheAccess=mCacheAccess;
            if (null!=cacheAccess) {
                mPaused = false;
                synchronized (cacheAccess) {
                    Debug.D(getClass(),"Resume start play media "+(null!=debug?debug:"."));
                    cacheAccess.notifyAll();
                    notifyStatusChange(START,getPlaying(null),null,debug);
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean seek(double seek,String debug){
        return seek(seek,null,debug);
    }

    public final boolean seek(double seek,Object arg,String debug){
        Playing playing=mPlaying;
        Playable media=null!=playing?playing.getMedia():null;
        if (null!=media&&(null==arg||media.equals(arg))){
            return playing.setSeek(seek,debug);
        }
        return false;
    }

    public final boolean stop(String debug) {
        return stop(null,debug);
    }

    public final boolean stop(Object arg,String debug) {
        Playing playing=mPlaying;
        if (null!=playing&&(null==arg||playing.equals(arg))){
            mPlaying=null;
            cleanCached("While stop media "+(null!=debug?debug:"."));
            Playable playable=playing.getMedia();
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
        stop(null,"While play new media."+playable);
        mPlaying=new Playing(playable,seek);
        notifyStatusChange(START,playable,null,"While play new media.");
        notify(cacheAccess,"While play new media.");
        return true;
    }

    public final String getCachePath() {
        return mCacheFile;
    }

    public final boolean isRunning(){
        return null!=mCacheAccess;
    }

    public final boolean post(Runnable runnable,int delay){
        Handler handler=null!=runnable?mHandler:null;
        return null!=handler&&handler.postDelayed(runnable,delay);
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
                notifyStatusChange(WAITING,getPlaying(null),null,"Cache file NULL.");
                cache.wait();
                mWaiting=false;
                Debug.D(getClass(), "Wake up after "+(null!=debug?debug:"."));
            }
        }
    }
}
