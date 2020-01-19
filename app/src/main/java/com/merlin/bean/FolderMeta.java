package com.merlin.bean;

import com.merlin.api.Page;

public class FolderMeta extends Page<FileMeta> {
    private String path;


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
