package com.merlin.bean;

import com.merlin.api.SectionData;

public class NasFolder extends SectionData<NasFile> {
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
