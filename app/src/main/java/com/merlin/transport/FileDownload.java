package com.merlin.transport;

import com.merlin.bean.FileMeta;

import java.net.HttpURLConnection;

public class FileDownload {
    private final FileMeta mFile;
    private final String mFolder;

    public FileDownload(FileMeta file,String folder){
        mFile=file;
        mFolder=folder;
    }

    public void onDownload() {
        String path=null!=mFile?mFile.getPath():null;
//        HttpURLConnection connection=new H
//        Class<T> cls,Scheduler subscribeOn, io.reactivex.Scheduler
//        observeOn,Object dither, com.merlin.api.Callback...callbacks
    }
}
