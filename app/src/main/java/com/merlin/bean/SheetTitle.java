package com.merlin.bean;

public class SheetTitle {
    private String id;
    private String title;
    private long size;

    public SheetTitle(String id,String title,long size){
        this.id=id;
        this.title=title;
        this.size=size;
    }

    public long getSize() {
        return size;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }
}
