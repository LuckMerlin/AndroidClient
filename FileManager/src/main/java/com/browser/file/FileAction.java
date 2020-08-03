package com.browser.file;

import com.merlin.bean.Path;
import com.merlin.lib.Cancel;

import java.io.Closeable;
import java.io.IOException;

abstract class FileAction extends Cancel {

    protected final void notify(String note, Path from, Path instant, Float progress, ProcessProgress callback){
        if (null!=callback){
            callback.onFileActionProgressChange(note,instant,progress);
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
