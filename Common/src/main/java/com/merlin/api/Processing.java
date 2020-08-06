package com.merlin.api;

import java.util.List;

public final class Processing<T> {
    private String id;
    private int position;
    private long time;
    private long createTime;
    private int duration;
    private List<T> data;

    public String getId() {
        return id;
    }

    public int getPosition() {
        return position;
    }

    public long getTime() {
        return time;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getDuration() {
        return duration;
    }

    public List<T> getData() {
        return data;
    }
}
