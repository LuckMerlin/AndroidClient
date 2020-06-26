package com.merlin.bean;

import com.merlin.api.PageData;

public class FolderData<T extends Path> extends PageData<T> {
    private String name;
    private String parent;
    private String pathSep;

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getPath(){
         String name=this.name;
         String parent=this.parent;
         String sep=this.pathSep;
         return null!=sep&&null!=parent&&null!=name?parent+(parent.endsWith(sep)?"":sep)+name:null;
    }

    public String getPathSep() {
        return pathSep;
    }

    public void setPathSep(String pathSep) {
        this.pathSep = pathSep;
    }
}
