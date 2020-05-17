package com.merlin.bean;

import java.util.List;

public final class Love<T extends Path> {
    private String title;
    private String content;
    private long createTime;
    private long time;
    private long id;
    private String permission;
    private String type;
    private long url;
    private List<T> image;

    public long getTime() {
        return time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public String getTitle() {
        return title;
    }

    public long getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public List<T> getImage() {
        return image;
    }
}
