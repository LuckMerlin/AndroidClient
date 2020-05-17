package com.merlin.media;

import com.merlin.api.What;
import com.merlin.client.__Client;
import com.merlin.debug.Debug;
import com.merlin.player.IPlayable;
import com.merlin.player.MediaBuffer;
import com.merlin.util.Closer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetMediaBuffer<T extends IPlayable>  extends MediaBuffer<T> {
    private Reader mReader;

    public NetMediaBuffer(T media, double seek){
        super(media,seek);
    }

    protected abstract  Call<ResponseBody> onResolvePlayCall(T media);

    @Override
    protected synchronized final boolean open(double seek, String debug) {
        final T media=getPlayable();
        final Call<ResponseBody> call=null!=media?onResolvePlayCall(media):null;
        if (null==call){
            Debug.D(getClass(),"Can't play media,Url invalid "+(null!=debug?debug:".")+" call="+call+" "+media);
            return false;
        }
        Debug.D(getClass(),"Play media "+media);
        Reader curr=mReader;
        if (null!=curr){
            curr.recycle("Before play new media.");
        }
        final Reader reader=mReader=new Reader();
        if (!reader.open(seek,debug)){
            return false;
        }
        reader.mWriteComplete=false;
        reader.mState= Reader.STATE_OPENING;
//        Debug.D(getClass(),"下载 "+ url);
        call.enqueue(new Callback<ResponseBody>() {
            private void finishRequest(int what,String debug){
                reader.mWriteComplete=true;
                reader.setCanceler(null);
                reader.mState= what;
                reader.mContentLength=null;
                reader.wakeUp(what,debug);
            }

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (null!=response&&response.isSuccessful()){
                            ResponseBody responseBody=response.body();
                            reader.mContentLength=responseBody.contentLength();
                            MediaType mediaType=null!=responseBody?responseBody.contentType():null;
                            String contentType=null!=mediaType?mediaType.subtype():null;
                            if (null!=contentType&&contentType.equals("octet-stream")){
                                InputStream is = null!=responseBody?responseBody.byteStream():null;
                                try {
                                    reader.mState= What.WHAT_SUCCEED;
                                    byte[] buffer = new byte[1024*1024];
                                    if (reader.mState!=What.WHAT_CANCEL){
                                        int len;
                                        boolean canceled=false;
                                        while ((len=is.read(buffer))>0){
                                            if (canceled=(reader.mState==What.WHAT_CANCEL)||
                                                    !reader.write(buffer,0,len,false)){
                                                canceled=true;
                                                break;
                                            }
                                        }
                                        if (!canceled){
                                            reader.write(buffer,0,0,true);
                                        }
                                        Debug.D(getClass(),"Net media file cache finish."+canceled+" "+len+" "+media);
                                        return;
                                    }
                                }catch (Exception e){
                                    Debug.E(getClass(),""+e);
                                    e.printStackTrace();
                                }finally {
                                    new Closer().close(is);
                                }
                            }
                            Debug.D(getClass(),"Invalid file stream length response. "+contentType+" "+media);
                            finishRequest(What.WHAT_ERROR_UNKNOWN,"Invalid file stream length response. "+media);
                        }
                    }
                }).start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Debug.D(getClass(),"Fail request file stream "+t+" "+media);
                finishRequest(What.WHAT_ERROR_UNKNOWN, "Fail request file stream "+media);
            }
        });
        reader.waitHere(Reader.STATE_OPENING,"For client open response "+media);
        int state=reader.mState;
        if (state==What.WHAT_SUCCEED||state== Reader.STATE_WRITE_UPDATE){
            Debug.D(getClass(),"File_ stream reply succeed."+media);
            return true;//Reply succeed,Now return true to prepare play
        }
        reader.recycle("While client response failed.state="+reader.mState);//
        return false;
    }

    @Override
    public Long getContentLength() {
        Reader reader=mReader;
        return null!=reader?reader.mContentLength:null;
    }

    public long getCurrentPosition() {
        Reader reader=mReader;
        return null!=reader?reader.mNextStart:null;
    }

    @Override
    protected final int read(byte[] buffer, int offset, int length) {
        Reader curr=mReader;
        return null!=curr?curr.read(buffer,offset,length):0;
    }

    @Override
    protected final boolean close(String debug) {
        Reader reader=mReader;
        return null!=reader?reader.recycle("While close media buffer "+(null!=debug?debug:".")):false;
    }

    @Override
    protected final boolean seek(double seek) {
        if (seek>0){
            Reader reader=mReader;
            Long length=null!=reader?reader.mContentLength:null;
            if (null!=length&&seek<length){
                long position=(long)(seek<1?(long)(length*seek):seek);
                reader.mNextStart=position;
                return true;
            }
        }
        return false;
    }

    @Override
    public final boolean isOpened() {
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
        private boolean mWriteComplete=false;
        private int mState=STATE_NORMAL;
        private RandomAccessFile mAccess;
        private Long mContentLength=null;
        private long mNextStart=0;
        private final Boolean mMutex=true;
        private __Client.Canceler mCanceler;

        public Reader(){
            mNextStart=0;
        }

        public boolean open(double seek,String debug){
                try {
                    final File cacheFile= File.createTempFile("mediaPlay",".temp");
                    if (null!=cacheFile){
                        cacheFile.deleteOnExit();
                        if (cacheFile.exists()&&cacheFile.isFile()){
                            mAccess=new RandomAccessFile(cacheFile,"rwd");
                            Debug.D(getClass(),"Succeed open media cache file "+(null!=debug?debug:".")+cacheFile);
                            return true;
                        }
                    }
                } catch (Exception e) {
                    Debug.E(getClass(), "Exception open media cache file " + (null != debug ? debug : ".") + " e=" + e, e);
                    closeIO(mAccess);
                    return false;
                }
            Debug.D(getClass(),"Failed open media cache file "+(null!=debug?debug:"."));
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

        boolean write(byte[] bytes,int offset,int length,boolean complete){
            RandomAccessFile fis=mAccess;
            if (null==bytes||bytes.length<=0){
                return false;
            }
            if (null==fis){
                Debug.W(getClass(),"Can't write bytes into cache file.fis="+fis+" ");
                return false;
            }
            try {
                boolean needWakeup=false;
                mWriteComplete = complete;
                synchronized (fis) {
                    if (length>0){
                        fis.seek(fis.length());//Make append
                        fis.write(bytes,offset,length);
                    }
                    long railPointer = fis.getFilePointer();
                    mState=complete?BUFFER_READ_FINISH_EOF:mState;
                    long nextStart = mNextStart;
                    if(nextStart<railPointer){
                        needWakeup=true;
                    }
                }

                if(needWakeup){
                    wakeUp(STATE_WRITE_UPDATE,"While write bytes succeed.");
                }
                return true;
            } catch (IOException e) {
                Debug.E(getClass(),"Exception write bytes into cache file.e="+e,e);
                return false;
            }
        }

        private boolean wakeUp(Integer state,String debug){
            mState=null!=state?state:mState;
            synchronized (mMutex){
//                Debug.D(getClass(),"Wakeup for "+(null!=debug?debug:"."));
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

        private void setCanceler(__Client.Canceler canceler){
            this.mCanceler=canceler;
        }

        private boolean recycle(String debug){
            Reader reader=mReader;
            if (null!=reader&&reader==this){
                __Client.Canceler canceler=mCanceler;
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
