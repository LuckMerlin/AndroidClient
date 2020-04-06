package com.merlin.browser;

import com.merlin.api.Canceler;
import com.merlin.api.OnApiFinish;
import com.merlin.bean.FileMeta;
import com.merlin.server.Retrofit;

import java.util.ArrayList;

public class FileUploadProcess extends FileProcess<FileMeta> {

    public FileUploadProcess(Object title, ArrayList<FileMeta> files, String folder, Integer coverMode){
        super(title,files);
    }

    @Override
    public Canceler onProcess(OnProcessUpdate update, OnApiFinish apiFinish, Retrofit retrofit) {
        return null;
    }
}
