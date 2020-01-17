package com.merlin.bean;

import java.util.List;

public class FileBrowserMeta {
    private String file;
    private String parent;
    private boolean directory;
    private int size;
    private double duration;
    private List<FileMeta_BK> data;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<FileMeta_BK> getData() {
        return data;
    }

    public void setData(List<FileMeta_BK> data) {
        this.data = data;
    }

}
