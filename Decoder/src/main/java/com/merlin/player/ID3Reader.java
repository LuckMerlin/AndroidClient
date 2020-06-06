package com.merlin.player;

import org.farng.mp3.filename.FilenameTag;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v1_1;
import org.farng.mp3.id3.ID3v2_2;
import org.farng.mp3.id3.ID3v2_3;
import org.farng.mp3.id3.ID3v2_4;
import org.farng.mp3.lyrics3.AbstractLyrics3;
import org.farng.mp3.lyrics3.Lyrics3v1;
import org.farng.mp3.lyrics3.Lyrics3v2;

import java.io.File;
import java.io.RandomAccessFile;

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
//            byte version=newCache[3];
//            byte subVersion=newCache[4];
//            byte flag=newCache[5];
//            int total_size = (newCache[6]&0x7F)*0x200000 +(newCache[7]&0x7F)*0x4000 +(newCache[8]&0x7F)*0x80 +(newCache[9]&0x7F);
//            new ID3v2(version,subVersion,flag);
//            Debug.D(getClass(),"exist "+version+" "+subVersion+" "+flag+" "+total_size);
        }
        return false;
    }

    private void dd(RandomAccessFile random){
        AbstractID3v2 id3v2tag=null;
        AbstractLyrics3 lyrics3tag=null;
        File mp3file;
        FilenameTag filenameTag;
        ID3v1 id3v1tag=null;
//        try {
//            id3v1tag = new ID3v1_1(random);
//        } catch (Exception var12) {
//        }
//        try {
//            if (id3v1tag == null) {
//                id3v1tag = new ID3v1(random);
//            }
//        } catch (Exception var11) {
//        }
        try {
            id3v2tag = new ID3v2_4(random);
        } catch (Exception var10) {
        }
//        try {
//            if (id3v2tag == null) {
//                id3v2tag = new ID3v2_3(random);
//            }
//        } catch (Exception var9) {
//        }
//
//        try {
//            if (id3v2tag == null) {
//                id3v2tag = new ID3v2_2(random);
//            }
//        } catch (Exception var8) {
//        }
//
//        try {
//            lyrics3tag = new Lyrics3v2(random);
//        } catch (Exception var7) {
//        }
//
//        try {
//            if (lyrics3tag == null) {
//                lyrics3tag = new Lyrics3v1(random);
//            }
//        } catch (Exception var6) {
//        }
    }
}
