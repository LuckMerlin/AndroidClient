package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;

public final class FileBuffer extends Buffer {
    private final String mPath;
    private FileInputStream mAccess;

     FileBuffer(Playable playable,double seek){
        super(playable,seek);
         mPath=null!=playable?playable.getPath():null;
    }

    @Override
    public boolean open(double seek,String debug) {
        String path=mPath;
        File file=null!=path&&!path.isEmpty()?new File(path):null;
        final long length=null!=file?file.length():-1;
        if (length<=0){
            Debug.W(getClass(),"Can't open media file.length="+length+" "+path);
            return false;
        }
        try {
            FileInputStream access=mAccess;
            if (null==mAccess){
                Debug.D(getClass(),"Open media file "+(null!=debug?debug:".")+" "+path);
                access=mAccess=new FileInputStream(path);
            }
            long position=(long)(seek>=0?seek<=1?(long)(length*seek):seek<length?seek:0:0);
            if (position>=0){
                access.skip(position);
            }
            return null!=access;
        } catch (Exception e) {
            Debug.E(getClass(),"Exception open file.e="+e,e);
            closeIO(mAccess);
            mAccess=null;
            return false;
        }
    }

    @Override
    protected int read(byte[] buffer, int offset,int length) {
        if (null==buffer||buffer.length<=0){
            Debug.W(getClass(),"File read failed with invalid buffer content."+buffer);
            return READ_FINISH_ARG_INVALID;
        }
        FileInputStream access=mAccess;
        if (null==access){
            return READ_FINISH_NOT_OPEN;
        }
        try {
            int dd= access.read(buffer,offset,length);
            return dd;
        } catch (Exception e) {
            Debug.E(getClass(),"File read exception."+mPath+ " e="+e,e);
            return READ_FINISH_EXCEPTION;
        }
    }

    @Override
    public boolean close(String debug) {
        FileInputStream access=mAccess;
        if (null!=access){
            Debug.D(getClass(),"Close file buffer "+(null!=debug?debug:".")+" "+mPath);
            mAccess=null;
            closeIO(access);
            return true;
        }
        return false;
    }

    @Override
    protected boolean seek(double seek) {
        return false;
    }

    @Override
    public boolean isOpened() {
        return null!=mAccess;
    }
}
