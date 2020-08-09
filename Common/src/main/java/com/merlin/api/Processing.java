package com.merlin.api;

import java.util.List;

public final class Processing<T,M> {
    private String id;
    private float position;
    private long time;
    private long createTime;
    private int duration;
    private List<T> data;
    private Reply<M> terminal;

    public String getId() {
        return id;
    }

    public float getPosition() {
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

    public Reply<M> getTerminal() {
        return terminal;
    }
}
