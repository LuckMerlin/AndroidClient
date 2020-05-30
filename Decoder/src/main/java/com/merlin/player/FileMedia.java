package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileMedia extends Media<String> {
    private FileInputStream mInput=null;
    private long mLength=0;

    public FileMedia(String path){
        super(path);
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
    public Integer read(byte[] buffer, int offset) throws IOException {
        FileInputStream input=mInput;
        int length=null!=buffer?buffer.length:-1;
        if (null==input||length<0||offset<0||offset>length){
            Debug.W(getClass(),"Fail read media file bytes which input is NULL.");
            return null;
        }
        if (length==offset){
            Debug.D(getClass(),"Already read full.");
            return Buffer.NORMAL;
        }
       return input.read(buffer,offset,length-offset);
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
