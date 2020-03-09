package com.merlin.bean;

import androidx.annotation.Nullable;

public final class FileUpload {

    private final String from;
    private final String to;
    private final String folder;

    public FileUpload(String from,String to,String directory){
        this.from=from;
        this.to=to;
        this.folder=directory;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileUpload){
            FileUpload up=(FileUpload)obj;
            return equals(up.from,from)&&equals(up.to,to);
        }
        return super.equals(obj);
    }

    private boolean equals(String a,String b){
        return (null==a&&null==b)||(null!=a&&null!=b&&a.equals(b));
    }

}
