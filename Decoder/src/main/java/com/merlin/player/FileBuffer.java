package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileInputStream;

public final class FileBuffer extends Buffer {
    private final String mPath;
    private FileInputStream mAccess;

     FileBuffer(Playable playable){
        super(playable);
         mPath=null!=playable?playable.getPath():null;
    }

    @Override
    public boolean open(String debug) {
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
            return null!=access;
        } catch (Exception e) {
            Debug.E(getClass(),"Exception open file.e="+e,e);
            closeIO(mAccess);
            mAccess=null;
//            finishOpenStep(false,true,STEP_FINISH_OPEN_FAILED,length,step,"Open failed."+file);
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
//            access.skip((int)(access.available()*0.6));
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
            finishCloseStep(true,STEP_FINISH_SUCCEED,"","Close succeed.");
            return true;
        }
        finishCloseStep(false,STEP_FINISH_ALREADY,"","Already closed.");
        return false;
    }

}
