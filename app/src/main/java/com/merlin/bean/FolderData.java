package com.merlin.bean;

import com.merlin.api.PageData;

public class FolderData<T extends FileMeta> extends PageData<T> {
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
