package com.browser.file;

import com.merlin.bean.Path;

import java.io.Closeable;
import java.io.IOException;

class FileAction {
    private boolean mCanceled=false;

    public final boolean isCanceled() {
        return mCanceled;
    }

    protected final void notify(String note, Path from,Path instant, Float progress, Progress callback){
        if (null!=callback){
            callback.onFileProgressChange(note,instant,progress);
        }
     }

    protected final boolean close(Closeable...closeables){
        if (null!=closeables&&closeables.length>0){
            for (Closeable child:closeables) {
                if (null!=child){
                    try {
                        child.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

}
