package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.lib.Cancel;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

abstract class FileAction<T> extends Cancel {

    protected final void notify(Float progress, String note, T instant, List<T> processed, OnProcessChange callback){
        if (null!=callback){
            callback.onProcessChanged(progress,note,instant,processed);
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
