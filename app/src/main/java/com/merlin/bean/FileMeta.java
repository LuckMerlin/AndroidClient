package com.merlin.bean;

import androidx.annotation.Nullable;

public class FileMeta {
    /**
     * file : F:\LuckMerlin\SLManager\.git
     * size : 4096
     * lastModifyTime : 1.5758874842258315E9
     * name : .git
     * directory : true
     * read : true
     * write : true
     * extension : .py
     */

    private String file;
    private int size;
    private double lastModifyTime;
    private String name;
    private boolean directory;
    private boolean read;
    private boolean write;
    private String extension;
    private int length;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

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

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isRead() {
        return read;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

    public final  boolean isPathEquals(String path){
        String filePath=file;
        return null!=path&&null!=filePath&&filePath.equals(path);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileMeta){
            return isPathEquals(((FileMeta)obj).getFile());
        }
        return super.equals(obj);
    }
}
