package com.merlin.bean;

import com.merlin.api.PageData;

public class FolderData<T extends Path> extends PageData<T> {
    private String name;
    private String parent;
    private String pathSep;
    private double total;
    private double free;

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

    public void setTotal(double total) {
        this.total = total;
    }

    public void setFree(double free) {
        this.free = free;
    }

    public String getPathSep() {
        return pathSep;
    }

    public void setPathSep(String pathSep) {
        this.pathSep = pathSep;
    }

    public double getTotal() {
        return total;
    }

    public double getFree() {
        return free;
    }
}
