package com.merlin.task.file;

import com.merlin.task.FromToTask;

import java.io.Closeable;
import java.io.IOException;

public abstract class FileTransTask<T,V> extends FromToTask<T,V> {
    private Integer mCover;
    private String mMethod;
    private boolean mBreakPoint=false;

    public FileTransTask(String name,T from, V to,String method) {
        super(name,from, to);
        mMethod=method;
    }

    public final void setMethod(String method) {
        this.mMethod = method;
    }

    public final String getMethod() {
        return mMethod;
    }

    public final boolean setCover(Integer cover) {
        mCover=cover;
        return true;
    }

    public final int getCover() {
        Integer cover=mCover;
        return null!=cover?cover:Cover.COVER_NONE;
    }

    public final boolean enableBreakPoint(boolean enable, String debug){

        return false;
    }

    public final boolean isEnableBreakPoint() {
        return mBreakPoint;
    }

    protected final void close(Closeable ...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables){
                if (null!=child){
                    try {
                        child.close();
                    } catch (IOException e) {
                        //Do nothing
                    }
                }
            }
        }
    }

}
