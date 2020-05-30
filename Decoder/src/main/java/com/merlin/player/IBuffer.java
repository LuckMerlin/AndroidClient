package com.merlin.player;

import java.io.InputStream;

abstract class IBuffer {
     public final static int EOF=-1;
     public final static int LOADING=-2;
     private final byte[] mBuffered;
     private int mStart,mEnd;
     private OnBytesLoadFinish mFinish;

     public IBuffer(int bufferSize){
         mBuffered=bufferSize>0?new byte[bufferSize]:null;
         resetBuffer();
     }

     public final void resetBuffer(){
         mStart=0;
         mEnd=0;
     }

     protected abstract void onLoadMore(OnBytesLoadFinish finish);

     public final int read(byte[] buffer,int offset,int size){
         int volume=null!=buffer?buffer.length:-1;
         if (volume<=0||offset<0||size<=0||(offset+size>volume)){
            return EOF;
         }
         byte[] buffered=mBuffered;
         int length=null!=buffered?buffered.length:-1;
         if (length<=0){
            return EOF;
         }
         int start=mStart;
         int end=mEnd;
         int copyLength=start<0?-1:Math.min(end-start,size);
         if (copyLength<length/2){//Trigger loading more
             if (null==mFinish){
                 onLoadMore(mFinish=(inputStream)->{
                     OnBytesLoadFinish curr=mFinish;
                     if (null!=curr&&curr==this){
                         if (null!=inputStream){
                             int currStart=mStart;
//                             inputStream.read(buffered,currStart);
                         }
                         mFinish = null;
                     }
                 });
             }
         }
         if (copyLength<=0){//If exist available
             return LOADING;
         }
         synchronized (buffered){
             System.arraycopy(buffered,start,buffer,offset,copyLength);
             mStart=start+copyLength;
             return copyLength;
         }
     }

     interface OnBytesLoadFinish{
         void onBytesLoadFinish(InputStream inputStream);
     }

}
