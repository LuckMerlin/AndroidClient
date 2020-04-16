package com.merlin.bean;

public class WebsiteImage extends Path{
    private long  id=-1;
    private String title;
    private String note;
    private boolean banner;
    private boolean background;

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public long getId() {
        return id;
    }
}
