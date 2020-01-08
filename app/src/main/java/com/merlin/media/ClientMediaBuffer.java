package com.merlin.media;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.Callback;
import com.merlin.player.MediaBuffer;
import com.merlin.protocol.What;
import com.merlin.util.FileMaker;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class ClientMediaBuffer extends MediaBuffer<Media> {
    private final Client mClient;
    private final String mCachePath;
    private Reader mReader;

    public ClientMediaBuffer(Client client,Media media, double seek){
        super(media,seek);
        mCachePath="/sdcard/a/temp.mp3";
        mClient=client;
    }

    @Override
    protected boolean open(double seek, String debug) {
        Client client=mClient;
        boolean login=null!=client&&client.isLogined();
        if (!login){
            Debug.D(getClass(),"Can't play media while failed open client url,Not login "+(null!=debug?debug:".")+" client="+client);
            return false;
        }
        final Media media=getPlayable();
        final String url=null!=media?media.getUrl():null;
        if (null==url||url.length()<=0){
            Debug.D(getClass(),"Can't play media,Url invalid "+(null!=debug?debug:".")+" url="+url+" "+media);
            return false;
        }
        String account=media.getAccount();
        Debug.D(getClass(),"Play media from "+account+" "+url);
        final String cachePath=mCachePath;
        Reader curr=mReader;
        if (null!=curr){
            curr.recycle("Before play new media.");
        }
        final Reader reader=mReader=new Reader(cachePath);
        if (!reader.open(seek,debug)){
            return false;
        }
        reader.mWriteComplete=false;
        reader.mState=Reader.STATE_OPENING;
        final int timeout=30*1000;
        Client.Canceler canceler=client.download(account,url,seek,timeout,(succeed, what, note, frame)->{
//            Debug.D(getClass(),"$$$$$$$$$ "+succeed+" "+what+" "+note);
            switch (what){
                case What.WHAT_NOT_EXIST:
                    Debug.D(getClass(),"File not exist."+account+" "+url);
                    reader.wakeUp(What.WHAT_NOT_EXIST,"Url file not exist."+account+" "+url);
                    reader.setCanceler(null);
                    break;
                case What.WHAT_NOT_ONLINE:
                    Debug.D(getClass(),"Client not online."+account+" "+url);
                    reader.wakeUp(What.WHAT_NOT_ONLINE,"Client not online."+account+" "+url);
                    reader.setCanceler(null);
                    break;
                case What.WHAT_HEAD_DATA:
                    Debug.D(getClass(),"Media file head got."+account+" "+url);
                    reader.wakeUp(What.WHAT_HEAD_DATA,"Client head response."+account+" "+url);
                    break;
                case Callback.REQUEST_SUCCEED://Bytes received
                    if (reader.mState!=What.WHAT_CANCEL){
                        byte[] bytes=null!=frame?frame.getBodyBytes():null;
                        if (null!=bytes&&bytes.length>0){
                            if (reader.write(bytes,frame.isLastFrame())){

                            }
                        }
                    }
                    break;
                default:
                    if (!succeed){//Not succeed
                        reader.mState=what;
                        reader.mWriteComplete=true;
                        reader.setCanceler(null);
                        reader.wakeUp(what," "+note+account+" "+url);
                    }
                    break;
            }
        });
        if (null==canceler){
            reader.recycle("While client request failed.");
            Debug.D(getClass(),"Failed play media while client request failed.canceler="+canceler);
            return false;
        }
        reader.setCanceler(canceler);
        reader.waitHere(Reader.STATE_OPENING,"For client open response "+account+" "+url);
        if (reader.mState==What.WHAT_HEAD_DATA){
            return true;//Response head,Now return true to prepare play
        }
        reader.recycle("While client response failed.state="+reader.mState);//
        return false;
    }

    @Override
    protected int read(byte[] buffer, int offset, int length) {
        Reader curr=mReader;
        return null!=curr?curr.read(buffer,offset,length):0;
    }

    @Override
    protected boolean close(String debug) {
        Reader reader=mReader;
        return null!=reader?reader.recycle("While close media buffer "+(null!=debug?debug:".")):false;
    }

    @Override
    protected boolean seek(double seek) {

        return false;
    }

    @Override
    public boolean isOpened() {
        Reader curr=mReader;
        RandomAccessFile access=null!=curr?curr.mAccess:null;
        return null!=access;
    }

    private class Reader {
        private final static int STATE_NORMAL=2010;
        private final static int STATE_OPENING=2011;
        private final static int STATE_OPEN_FAILED=2012;
        private final static int STATE_WAITING_WRITE=2013;
        private final static int STATE_WRITE_UPDATE=2014;
        private final static int BUFFER_SIZE=1014*1024;
        private final String mCacheFile;
        private boolean mWriteComplete=false;
        private int mState=STATE_NORMAL;
        private RandomAccessFile mAccess;
        private long mNextStart=0;
        private final Boolean mMutex=true;
        private Client.Canceler mCanceler;

        public Reader(String cacheFile){
            mCacheFile=cacheFile;
            mNextStart=0;
        }

        public boolean open(double seek,String debug){
            String path=mCachePath;
            File file=null!=path&&path.length()>0?new FileMaker().makeFile(path,true):null;
            if (null!=file&&file.exists()&&file.isFile()){
                try {
                    mAccess=new RandomAccessFile(file,"rwd");
                    Debug.D(getClass(),"Succeed open media cache file "+(null!=debug?debug:".")+file);
                    return true;
                } catch (Exception e) {
                    Debug.E(getClass(),"Exception open media cache file "+(null!=debug?debug:".")+file+" e="+e,e);
                    closeIO(mAccess);
                    return false;
                }
            }
            Debug.D(getClass(),"Failed open media cache file "+(null!=debug?debug:".")+" "+file);
            return false;
        }

        int read(byte[] buffer, int offset, int length) {
            final int bufferLength=null!=buffer?buffer.length:-1;
            if (bufferLength>0&&offset>=0&&length>0&&(offset+length)<=bufferLength){
                 RandomAccessFile access=mAccess;
                 if (null!=access){
                     try {
                         while (true) {
                             long nextStart = mNextStart;
                             long cacheFileLength;
                             synchronized (access) {
                                 cacheFileLength = access.length();
                             }
//                             Debug.D(getClass(), "%%%%% cacheFileLength %%%%%% " +
//                                     cacheFileLength + " " + nextStart);
                             if (nextStart >= cacheFileLength) {//Not enough cached bytes,Need wait here
                                 if (mWriteComplete){
                                     return BUFFER_READ_FINISH_EOF;
                                 }
                                 waitHere(STATE_WAITING_WRITE, "While cache not enough for read." + nextStart + " " + cacheFileLength);
                             }else {
                                 int readSize;
                                 synchronized (access) {
                                     access.seek(nextStart);
                                     readSize=access.read(buffer,offset,length);
                                 }
                                 if (readSize>0){
                                     mNextStart+=readSize;
                                     return readSize;
                                 }
                             }
                         }
                     } catch (IOException e) {
                         Debug.E(getClass(),"Exception read from buffer e="+e,e);
                         return BUFFER_READ_FINISH_EXCEPTION;
                     }
                 }
                 return READ_FINISH_NOT_OPEN;
            }
            return READ_FINISH_ARG_INVALID;
        }

        boolean write(byte[] bytes,boolean complete){
            RandomAccessFile fis=mAccess;
            if (null==bytes||bytes.length<=0){
                return false;
            }
            if (null==fis){
                Debug.W(getClass(),"Can't write bytes into cache file.fis="+fis+" "+mCacheFile);
                return false;
            }
            try {
                boolean needWakeup=false;
                synchronized (fis) {
                    long length = fis.length();
                    fis.seek(length);//Make append
                    fis.write(bytes);
                    long railPointer = fis.getFilePointer();
                    mWriteComplete = complete;
                    mState=complete?BUFFER_READ_FINISH_EOF:mState;
                    long nextStart = mNextStart;
                    if(nextStart>=length&&nextStart<railPointer){
                        needWakeup=true;
                    }
                }
                if(needWakeup){
                    wakeUp(STATE_WRITE_UPDATE,"While write bytes succeed.");
                }
            } catch (IOException e) {
                Debug.E(getClass(),"Exception write bytes into cache file.e="+e+" "+mCacheFile,e);
                return false;
            }
          return false;
        }

        private boolean wakeUp(Integer state,String debug){
            mState=null!=state?state:mState;
            synchronized (mMutex){
                Debug.D(getClass(),"Wakeup for "+(null!=debug?debug:"."));
                mMutex.notify();
            }
            return false;
        }

        private void waitHere(Integer state,String debug){
            mState=null!=state?state:mState;
            synchronized (mMutex){
                try {
                    Debug.D(getClass(),"Wait for "+(null!=debug?debug:"."));
                    mMutex.wait();
//                    Debug.D(getClass(),"Has been wakeup.");
                } catch (InterruptedException e) {
                    Debug.E(getClass(),"Can't wait for "+(null!=debug?debug:".")+" e="+e,e);
                }
            }
        }

        private void setCanceler(Client.Canceler canceler){
            this.mCanceler=canceler;
        }

        private boolean recycle(String debug){
            Reader reader=mReader;
            if (null!=reader&&reader==this){
                Client.Canceler canceler=mCanceler;
                Debug.D(getClass(),"Recycle media reader "+(null!=debug?debug:"."));
                mReader=null;
                reader.mCanceler=null;
                closeIO(mAccess);
                mAccess=null;
                if (null!=canceler&&canceler.cancel(true)){
                    waitHere(What.WHAT_CANCEL,"While need wait cancel.");
                }
                return true;
            }
            return false;
        }
    }
}
