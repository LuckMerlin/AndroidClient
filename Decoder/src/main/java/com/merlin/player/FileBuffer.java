package com.merlin.player;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.crypto.spec.DESedeKeySpec;

public final class FileBuffer extends Buffer {
    private final String mFile;
    private RandomAccessFile mAccess;

     FileBuffer(Playable playable){
        super(playable);
        mFile=null!=playable?playable.getPath():null;
    }

    @Override
    public boolean open(String debug) {
        String path=mFile;
        File file=null!=path&&!path.isEmpty()?new File(path):null;
        final long length=null!=file?file.length():-1;
        if (length<=0){
//            finishOpenStep(false,false,STEP_FINISH_OPEN_FAILED,length,step,"File invalid.length="+length+" "+file);
            return false;
        }
        try {
            RandomAccessFile access=mAccess=null!=mAccess?mAccess:new RandomAccessFile(mFile,"r");
//            finishOpenStep(true,true,STEP_FINISH_SUCCEED,length,step,"Open failed."+file);
            return null!=access;
        } catch (Exception e) {
            closeIO(mAccess);
            mAccess=null;
            Debug.E(getClass(),"Open file exception.e="+e,e);
//            finishOpenStep(false,true,STEP_FINISH_OPEN_FAILED,length,step,"Open failed."+file);
            return false;
        }
    }

    @Override
    protected int read(byte[] buffer, double seek) {
        if (null==buffer||buffer.length<=0){
            Debug.W(getClass(),"File read failed with invalid buffer content."+buffer);
            return READ_FINISH_ARG_INVALID;
        }
        RandomAccessFile access=mAccess;
        if (null==access){
            return READ_FINISH_NOT_OPEN;
        }
        if (seek>=0){
            try {
                long position=(long)(seek>0&seek<=1?(seek*access.length()):seek);
                access.seek(position);
            } catch (IOException e) {
                Debug.E(getClass(),"File seek exception."+mFile+" e="+e,e);
                return READ_FINISH_EXCEPTION;
            }
        }
        try {
            int dd= access.read(buffer);
            return dd;
        } catch (IOException e) {
            Debug.E(getClass(),"File read exception."+mFile+ " e="+e,e);
            return READ_FINISH_EXCEPTION;
        }
    }

    @Override
    protected boolean onClose(CloseStep step, String debug) {
        RandomAccessFile access=mAccess;
        if (null!=access){
            Debug.D(getClass(),"Stop file buffer "+(null!=debug?debug:".")+" "+mFile);
            mAccess=null;
            closeIO(access);
            finishCloseStep(true,STEP_FINISH_SUCCEED,step,"Stop succeed.");
            return true;
        }
        finishCloseStep(false,STEP_FINISH_ALREADY,step,"Already closed.");
        return false;
    }

}
