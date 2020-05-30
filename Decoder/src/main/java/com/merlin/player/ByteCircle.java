package com.merlin.player;

import java.io.IOException;
import java.io.InputStream;

public class ByteCircle {
//    private final Volume mVolume=new Volume();
    public static final int EOF=-1;
    public static final int FULL=-2;
    public static final int FATAL_ERROR=-3;
    private final byte[] mCache=new byte[1024];
    private final byte[] mRead=new byte[1024];
    private int mReadCursor=-1;

    public final int read(byte[] buffer,int offset){
        int readCursor=mReadCursor;
//        if (readCursor>=0&&readCursor<){
//
//        }
        return -1;
    }
//    private static class Volume{
//        private final byte[] mCache=new byte[1024];
//        private final byte[] m=new byte[1024];
//        private Integer mCursor=0;
//
//        int read(byte[] buffer,int offset){
//            int length=null!=buffer?buffer.length:-1;
//            if (length<=0||offset<0){
//                return EOF;
//            }
//            synchronized (mCursor){
//
//            }
//            return EOF;
//        }
//
//        int write(InputStream inputStream,long skip) throws IOException {
//            if (null==inputStream){
//                return FATAL_ERROR;
//            }
//            Integer cursor=mCursor;
//            synchronized (max){
//                byte[] bytes=mBytes;
//                int length=null!=bytes?bytes.length:-1;
//                if (length<=0||max<0||max>length){
//                    return FATAL_ERROR;
//                }
//                if (max==length){
//                    return FULL;
//                }
//                if (skip>0) {
//                    inputStream.skip(skip);
//                }
//                int read=-1;
//                while ((read=inputStream.read(bytes,0,cursor))>=0){
//                    //Write into tail firstly
//                    cursor
//                }
//                inputStream.read(bytes,,0);
//            }
//            return -1;
//        }
//    }

    public final int write(InputStream inputStream,long skip){
//        return mVolume.write(inputStream,skip);
        return -1;
    }

//    private final static class Reader{
//        private final byte[] mCache=new byte[1024];
//        private int mCursor=0;
//
//        int peekAll(byte[] buffer,int offset){
//            if (null==buffer||buffer.length!=mCache.length){
//                return FATAL_ERROR;
//            }
//            synchronized (mCache){
//
//            }
//            return -1;
//        }
//    }
}
