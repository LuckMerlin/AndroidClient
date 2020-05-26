package com.merlin.player;

 class Buffer{
     private final byte[] mBuffered;
     private int mStart,mEnd;

     public Buffer(int bufferSize){
         mBuffered=bufferSize>0?new byte[bufferSize]:null;
         resetBuffer();
     }

     public final void resetBuffer(){
         mStart=0;
         mEnd=0;
     }

     protected int onLoad(byte[] buffer,int offset,int size){
         return -1;
     }

     public boolean read(byte[] buffer,int offset,int size){
         int volume=null!=buffer?buffer.length:-1;
         if (volume<=0||offset<0||size<=0||(offset+size>volume)){
            return false;
         }


         return false;
     }

}
