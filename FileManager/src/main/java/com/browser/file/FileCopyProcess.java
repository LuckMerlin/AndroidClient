package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.retrofit.Retrofit;

import java.util.ArrayList;

public class FileCopyProcess extends FileProcess<Path>{

    public FileCopyProcess(String title, ArrayList<Path> files, Path folder, int coverMode){

    }


    @Override
    protected Reply<Path> onProcess(Path pathObj, OnProcessChange<Path> processProgress, Retrofit retrofit) {
        return null;
    }
}
