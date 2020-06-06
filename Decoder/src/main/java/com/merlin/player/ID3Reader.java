package com.merlin.player;

import android.provider.Settings;

import com.merlin.debug.Debug;

import java.util.Map;

public class ID3Reader {
    private byte[] mHeadBytes;

    public final Object onWrite(byte[] buffer, int offset, int size){
        int length=null!=buffer?buffer.length:-1;
        if (length<=0||offset<0||size<=0||length<offset+size){
            return false;
        }
        final int headLength=10;
        byte[] cache=mHeadBytes;
        mHeadBytes=null;//Reset
        int cachedLength=null!=cache?cache.length:0;
        int increaseLength=Math.min(headLength-cachedLength,size);
        byte[] newCache=new byte[cachedLength+increaseLength];
        if (cachedLength>0){//Copy old cache first
            System.arraycopy(cache,0,newCache,0,cachedLength);
        }
        System.arraycopy(buffer,offset,newCache,cachedLength,increaseLength);//Copy new write
        if (newCache.length<10){
            mHeadBytes=newCache;
            return this;
        }
        if (newCache[0]=='I'&&newCache[1]=='D'&&newCache[2]=='3'){
            byte version=newCache[3];
            byte subVersion=newCache[4];
            byte flag=newCache[5];
            int total_size = (newCache[6]&0x7F)*0x200000 +(newCache[7]&0x7F)*0x4000 +(newCache[8]&0x7F)*0x80 +(newCache[9]&0x7F);
            new ID3v2(version,subVersion,flag);
            Debug.D(getClass(),"exist "+version+" "+subVersion+" "+flag+" "+total_size);
        }
        return false;
    }
}
