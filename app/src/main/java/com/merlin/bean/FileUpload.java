package com.merlin.bean;

import androidx.annotation.Nullable;

public final class FileUpload {

    private final String mFrom;
    private final String mTo;
    private final boolean mDirectory;
    public FileUpload(String from,String to,boolean directory){
        mFrom=from;
        mTo=to;
        mDirectory=directory;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileUpload){
            FileUpload up=(FileUpload)obj;
            return equals(up.mFrom,mFrom)&&equals(up.mTo,mTo);
        }
        return super.equals(obj);
    }

    private boolean equals(String a,String b){
        return (null==a&&null==b)||(null!=a&&null!=b&&a.equals(b));
    }

}
