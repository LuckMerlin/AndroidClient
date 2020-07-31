package com.merlin.bean;

import com.merlin.api.PageData;

import java.util.ArrayList;

public class Folder<T extends Path> extends PageData<T> {
    private final Path folder;

    public Folder(Path folder){
        this(folder,0,null,-1);
    }

    public Folder(Path folder,int from, ArrayList<T> data, long length){
        super(from,data,length);
        this.folder=folder;
    }

    public Path getPath() {
        return folder;
    }
}
