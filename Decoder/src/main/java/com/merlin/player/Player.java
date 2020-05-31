package com.merlin.player;
import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Player{
    public final static int  NORMAL = 0;
    public final static int  FATAL_ERROR = -2;
    public final static int  IDLE = -2003;
    public final static int  STOP =  -2005;
    public final static int  PROGRESS =  -2006;
    public final static int  PLAYING =  -2007;
    public final static int  CREATE =  -2021;
    public final static int  DESTROY =  -2022;
    private final String mCacheFile;
    private RandomAccessFile mCacheAccess;
    private Playing mPlaying;
    private boolean mPaused=false;

    static {
        System.loadLibrary("linqiang");
    }

    public Player(){
        this(null);
    }

    public Player(String cacheFile){
        mCacheFile=cacheFile;
    }

    public final synchronized boolean run() {
        if (null!=mCacheAccess){
            return false;
        }
        String cachePath=mCacheFile;
        File cacheFile=null;
        if (null==cachePath||cachePath.length()<=0){
            try {
                cacheFile = File.createTempFile("player","cache");
            } catch (IOException e) {
                Debug.E(getClass(),"Can't run player while cache file create exception.e="+e,e);
                e.printStackTrace();
                return false;
            }
        }else{
            cacheFile=new File(cachePath);
        }
        if (null==cacheFile){
            Debug.W(getClass(),"Can't run player while cache file NULL.");
            return false;
        }
        try {
            cacheFile.deleteOnExit();
            final RandomAccessFile cacheAccess=new RandomAccessFile(cacheFile,"rw");
            final byte[] cacheBuffer=new byte[1024*1024];
            final OnLoadMedia loader=(byte[] playerBuffer, int playerOffset) ->{
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
                Playing playing=mPlaying;
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
                long length=cacheAccess.length();
                long loadedCursor=playing.mCursor;
                loadedCursor=loadedCursor<=0?0:loadedCursor;
                Long totalLength=playing.mLength;
                if ((null==totalLength||length<totalLength)&&(length<=0||loadedCursor>=length)){//Empty
                    final Looper currLooper=Looper.myLooper();
                    final Integer[] cacheLoad=new Integer[1];
                    if (!media.cache((what,inputStream,total)-> {
                        if (null!=inputStream){
                            Looper myLooper=Looper.myLooper();
                            new Handler(currLooper==myLooper?Looper.getMainLooper():myLooper).post(()->{
                                long totalWritten=0;
                                try {
                                    do {
                                        long currCursor=playing.mCursor;
                                        long currLength=cacheAccess.length();
                                        if (currLength>currCursor){
                                            Debug.D(getClass(),"通知 "+currLength+" "+currCursor);
                                            notify(cacheAccess,"After length more than cursor."+currCursor+" "+currLength);
                                        }
                                        int read=inputStream.read(cacheBuffer,0,cacheBuffer.length);
                                        if (read>0) {
                                            totalWritten+=read;
                                            Debug.D(getClass(),"写入 "+totalWritten);
                                            cacheAccess.write(cacheBuffer, 0, read);
                                        }else if (read<0){
                                            playing.mLength=totalWritten;
                                            Debug.D(getClass(),"Cache media finish."+totalWritten+" "+cacheAccess.length());
                                            break;
                                        }
                                    }while(true);
                                }catch (Exception  e){
                                    e.printStackTrace();
                                }
                            });
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
                }else if (loadedCursor<=length){//Need load
                     int read= cacheAccess.read(playerBuffer,playerOffset,playerBufferLength-playerOffset);
                     if (read>0) {
                         Debug.D(getClass(),"读取 "+read);
                         playing.mCursor += read;
                     }
                     return read;
                }
                Debug.D(getClass(),"Loadisdfa是的发生 ng "+length);
                return NORMAL;
            };
            mCacheAccess=cacheAccess;
            new Thread(new PlayerRunnable("Player"){
                @Override
                public void run() {
                    Debug.D(getClass(),"SSSSSSSEEEEE Player begin");
                    create(loader);
                    RandomAccessFile curr=mCacheAccess;
                    if (null!=curr&&curr==cacheAccess){
                        mCacheAccess=null;
                    }
                    Debug.D(getClass(),"SSSSSSSEEEEE Player end");
                }
            }).start();
            return true;
        } catch (FileNotFoundException e) {
            Debug.E(getClass(),"Can't run player while run exception.e="+e,e);
            e.printStackTrace();
            return false;
        }
    }

    public final synchronized boolean release(){
        RandomAccessFile accessFile=mCacheAccess;
        if (null==accessFile){
            return false;
        }
        Debug.D(getClass(),"Release player.");
        mCacheAccess=null;
        stop("While release player.");
        synchronized (accessFile) {
            accessFile.notify();
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

    public final boolean stop(String debug){
        Playing playing=mPlaying;
        if (null!=playing){
            mPlaying=null;
            Playable playable=playing.mMedia;
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
        final Playing curr=mPlaying;
        Playable media=null!=curr?curr.mMedia:null;
        if (null!=media&&media.equals(playable)){
            Debug.W(getClass(),"Can't play media while already playing.");
            return false;
        }
        mPlaying=new Playing(playable);
        notify(cacheAccess,"While play new media.");
        return true;
    }

    public final boolean isRunning(){
        return null!=mCacheAccess;
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
        private long mCursor;
        private final Playable mMedia;
        private Long mLength=null;

        private Playing(Playable media){
            mMedia=media;
            mCursor=0;
        }
    }

    private void notify(RandomAccessFile cache,String debug)
    {
        if (null!=cache) {
            synchronized (cache) {
                Debug.D(getClass(), "Notify "+(null!=debug?debug:"."));
                cache.notify();
            }
        }
    }

    private void wait(RandomAccessFile cache,String debug) throws InterruptedException {
        if (null!=cache) {
            synchronized (cache) {
                Debug.D(getClass(), "Wait "+(null!=debug?debug:"."));
                cache.wait();
                Debug.D(getClass(), "Wake up after "+(null!=debug?debug:"."));
            }
        }
    }
}
