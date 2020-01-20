package com.merlin.bean;

import com.merlin.api.Page;

public class FolderMeta extends Page<FileMeta> {
    private String path;
    private String parent;


    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
