package com.merlin.transport;

import com.merlin.bean.INasFile;

public class FileDownload {
    private final INasFile mFile;
    private final String mFolder;

    public FileDownload(INasFile file, String folder){
        mFile=file;
        mFolder=folder;
    }

    public void onDownload() {
        String path=null!=mFile?mFile.getPath(null):null;
//        HttpURLConnection connection=new H
//        Class<T> cls,Scheduler subscribeOn, io.reactivex.Scheduler
//        observeOn,Object dither, com.merlin.api.Callback...callbacks
    }
}
