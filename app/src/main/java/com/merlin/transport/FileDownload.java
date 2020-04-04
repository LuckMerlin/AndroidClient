package com.merlin.transport;

import com.merlin.bean.NasFile;

public class FileDownload {
    private final NasFile mFile;
    private final String mFolder;

    public FileDownload(NasFile file, String folder){
        mFile=file;
        mFolder=folder;
    }

    public void onDownload() {
        String path=null!=mFile?mFile.getPath(false):null;
//        HttpURLConnection connection=new H
//        Class<T> cls,Scheduler subscribeOn, io.reactivex.Scheduler
//        observeOn,Object dither, com.merlin.api.Callback...callbacks
    }
}
