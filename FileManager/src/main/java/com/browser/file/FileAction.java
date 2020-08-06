package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.bean.Path;
import com.merlin.lib.Cancel;

import java.io.Closeable;
import java.io.IOException;

abstract class FileAction extends Cancel {

    protected final void notify(String note, Path from, Path instant, Float progress, OnProcessChange callback){
        if (null!=callback){
            callback.onProcessChanged(progress,note,instant,progress);
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
