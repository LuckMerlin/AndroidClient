package com.merlin.bean;

public final class File {


    /**
     * size : 4096
     * lastModifyTime : 1.5758523235349355E9
     * name : .git
     * exist : true
     * isFile : false
     * read : true
     * write : true
     * extension : .py
     */

    private int size;
    private double lastModifyTime;
    private String name;
    private boolean exist;
    private boolean isFile;
    private boolean read;
    private boolean write;
    private String extension;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(double lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isIsFile() {
        return isFile;
    }

    public void setIsFile(boolean isFile) {
        this.isFile = isFile;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
