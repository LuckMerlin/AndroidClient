package com.browser.file;

import com.merlin.bean.Path;

class FileAction {
    private boolean mCanceled=false;

    public final boolean isCanceled() {
        return mCanceled;
    }

    protected final void notify(String note, Path instant, Float progress, Progress callback){
        if (null!=callback){
            callback.onFileProgressChange(note,instant,progress);
        }
     }
}
