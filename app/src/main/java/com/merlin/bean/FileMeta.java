package com.merlin.bean;

public final class FileMeta {
    private String id;
    private String path;
    private String md5;
    private String mime;
    private String extension;
    private double createTime;
    private double modifyTime;
    private String permission;
    private long size;
    private double insertTime;
    private int childCount;
    private String extra;
    private String image;
    private String title;

    public boolean isDirectory(){
        return childCount>=0;
    }

    public long getSize() {
        return size;
    }

    public String getExtension() {
        return extension;
    }

    public double getModifyTime() {
        return modifyTime*1000;
    }

    public boolean isRead(){
        return false;
    }

    public String getPath() {
        return path;
    }

    public int getChildCount() {
        return childCount;
    }

    public String getTitle() {
        return title;
    }
}
