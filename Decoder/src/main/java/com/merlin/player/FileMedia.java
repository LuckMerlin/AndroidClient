package com.merlin.player;

import com.luckmerlin.core.debug.Debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class FileMedia implements BytesMedia {
    private RandomAccessFile mInput=null;
    private Meta mMeta;
    private final String mPath;

    public FileMedia(String path){
        mPath=path;
    }

    @Override
    public final boolean open(Player player) {
        final String mediaPath=mPath;
        if (null==mediaPath||mediaPath.length()<=0){
            Debug.W("Can't open media file which path is invalid."+mediaPath);
            return false;
        }
        RandomAccessFile input=mInput;
        if (input!=null){//Already opened
            return false;
        }
        try {
            File file=new File(mediaPath);
            final long length=file.length();
            String name=file.getName();
            mMeta=new Meta(length,name,null!=name?name.replace("*.",""):null,null);
            if (length<=0){

                Debug.W("Can't open media file which length is invalid."+mediaPath);
                return false;
            }
            mInput=new RandomAccessFile(file,"r");
            Debug.D("Opened media file."+mediaPath);
            return true;
        } catch (FileNotFoundException e) {
            Debug.E("Exception while open file media.e="+e+" "+mediaPath,e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public final boolean isOpened() {
        RandomAccessFile input=mInput;
        return null!=input&&null!=input.getChannel();
    }

    @Override
    public final boolean cache(Player player,CacheReady cacheReady) {
//        if (null!=cacheReady){
//            FileInputStream input=mInput;
//            cacheReady.onCacheReady(null==input?Player.FATAL_ERROR:Player.NORMAL,input,mLength);
//            return true;
//        }
        //Do nothing
        return false;
    }

    @Override
    public final int read(byte[] buffer, int offset,long loadCursor) throws IOException {
        RandomAccessFile input=mInput;
        int length=null!=buffer?buffer.length:-1;
        if (null==input||length<0||offset<0||offset>length){
            Debug.W("Fail read media file bytes which input is NULL.");
            return Player.FATAL_ERROR;
        }
        Meta meta=mMeta;
        if (null!=meta&&loadCursor>=0&&loadCursor<=meta.getLength()){
            input.seek(loadCursor);
        }
        return input.read(buffer,offset,length-offset);
    }

    @Override
    public final boolean close(Player player) {
        RandomAccessFile input=mInput;
        if (null!=input){
            mInput=null;
            try {
                input.close();
                Debug.D("Closed media file."+this);
                return true;
            } catch (IOException e) {
                Debug.E("Exception while close media file.e="+e+" "+this,e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public final Meta getMeta() {
        return mMeta;
    }

    @Override
    public String toString() {
        Meta meta=mMeta;
        return (null!=meta?meta:"")+super.toString();
    }
}
