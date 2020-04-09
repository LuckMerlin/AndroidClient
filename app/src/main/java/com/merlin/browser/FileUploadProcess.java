package com.merlin.browser;

import com.merlin.api.Canceler;
import com.merlin.api.OnApiFinish;
import com.merlin.bean.Document;
import com.merlin.server.Retrofit;

import java.util.ArrayList;

public class FileUploadProcess extends FileProcess<Document> {

    public FileUploadProcess(Object title, ArrayList<Document> files, String folder, Integer coverMode){
        super(title,files);
    }

    @Override
    public Canceler onProcess(OnProcessUpdate update, OnApiFinish apiFinish, Retrofit retrofit) {
        return null;
    }
}
