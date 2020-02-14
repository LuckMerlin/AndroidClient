package com.merlin.media;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.bean.Media;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.file.DownloadApi;
import com.merlin.player.MediaBuffer;
import com.merlin.retrofit.Retrofit;
import com.merlin.util.Closer;
import com.merlin.util.FileMaker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import com.merlin.api.What;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public final class NetMediaBuffer extends MediaBuffer<Media> {
    private final String mCachePath;
    private Reader mReader;
    private final Retrofit mRetrofit=new Retrofit();

    public NetMediaBuffer(Media media, double seek){
        super(media,seek);
        mCachePath="/sdcard/a/temp2.mp3";
    }

    @Override
    protected boolean open(double seek, String debug) {
        final Retrofit retrofit=mRetrofit;
        if (null==retrofit){
            Debug.D(getClass(),"Can't play media "+(null!=debug?debug:".")+" retrofit="+retrofit);
            return false;
        }
        final Media media=getPlayable();
        final String url=null!=media?media.getPath():null;
        if (null==url||url.length()<=0){
            Debug.D(getClass(),"Can't play media,Url invalid "+(null!=debug?debug:".")+" url="+url+" "+media);
            return false;
        }
        Debug.D(getClass(),"Play media  "+url);
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
        reader.mState= Reader.STATE_OPENING;
        Debug.D(getClass(),"下载 "+Address.URL+url);
        retrofit.call(DownloadApi.class, Schedulers.newThread()).downloadFile(url,true).enqueue(new Callback<ResponseBody>() {
                    private void finishRequest(int what,String debug){
                        reader.mWriteComplete=true;
                        reader.setCanceler(null);
                        reader.mState= what;
                        reader.wakeUp(what,debug);
                    }

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                        if (null!=response&&response.isSuccessful()){
                            ResponseBody responseBody=response.body();
                            MediaType mediaType=null!=responseBody?responseBody.contentType():null;
                            String contentType=null!=mediaType?mediaType.subtype():null;
//                            Debug.D(getClass(),"contentType "+contentType);
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
                                            Debug.D(getClass(),"Net media file cache finish."+canceled+" "+len+" "+url);
                                            return;
                                        }
                                }catch (Exception e){
                                    Debug.E(getClass(),""+e);
                                    e.printStackTrace();
                                }finally {
                                    new Closer().close(is);
                                }
                            }
                            Debug.D(getClass(),"Invalid file stream length response. "+contentType+" "+url);
                            finishRequest(What.WHAT_ERROR_UNKNOWN,"Invalid file stream length response. "+url);
                        }


                            }
                        }).start();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Debug.D(getClass(),"Fail request file stream "+url);
                        finishRequest(What.WHAT_ERROR_UNKNOWN, "Fail request file stream "+url);
                    }
                });
        reader.waitHere(Reader.STATE_OPENING,"For client open response "+url);
        int state=reader.mState;
        if (state==What.WHAT_SUCCEED||state==Reader.STATE_WRITE_UPDATE){
            Debug.D(getClass(),"File stream reply succeed."+url);
            return true;//Reply succeed,Now return true to prepare play
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

        boolean write(byte[] bytes,int offset,int length,boolean complete){
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
                Debug.E(getClass(),"Exception write bytes into cache file.e="+e+" "+mCacheFile,e);
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
