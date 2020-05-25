package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileMedia extends Media<String> {
    private FileInputStream mInput=null;
    private long mLength=0;

    public FileMedia(String path,int bufferSize){
        super(path,bufferSize);
    }

    @Override
    public final boolean open() {
        final String mediaPath=getSrc();
        if (null==mediaPath||mediaPath.length()<=0){
            Debug.W(getClass(),"Can't open media file which path is invalid."+mediaPath);
            return false;
        }
        FileInputStream input=mInput;
        if (input!=null){//Already opened
            return false;
        }
        try {
            File file=new File(mediaPath);
            mLength=file.length();
            if (mLength<=0){
                Debug.W(getClass(),"Can't open media file which length is invalid."+mediaPath);
                return false;
            }
            mInput=new FileInputStream(file);
            Debug.D(getClass(),"Opened media file."+mediaPath);
            return true;
        } catch (FileNotFoundException e) {
            Debug.E(getClass(),"Exception while open file media.e="+e+" "+mediaPath,e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public final boolean isOpened() {
        FileInputStream input=mInput;
        return null!=input&&null!=input.getChannel();
    }

    @Override
    protected int onReadBytes(long start, int offset, byte[] buffer,int size) {
        FileInputStream input=mInput;
        if (null==input){
            Debug.W(getClass(),"Fail read media file bytes which input is NULL.");
            return -1;
        }
        if (size<=0){
            Debug.W(getClass(),"Fail read media file bytes which offset or buffer invalid."+offset+" "+buffer);
            return -1;
        }
        try {
            if (start>0&&start<mLength){
                input.skip(start);
            }
            return input.read(buffer,offset,size);
        } catch (IOException e) {
            Debug.E(getClass(),"Exception read media file bytes.e="+e+" "+this,e);
            e.printStackTrace();
            return Player.FATAL_ERROR;
        }
    }

    @Override
    public Meta getMeta() {
        return new Meta(mLength);
    }

    @Override
    public final boolean close() {
        FileInputStream input=mInput;
        if (null!=input){
            mInput=null;
            mLength=0;
            try {
                input.close();
                Debug.D(getClass(),"Closed media file."+this);
                return true;
            } catch (IOException e) {
                Debug.E(getClass(),"Exception while close media file.e="+e+" "+this,e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
