package com.merlin.api;

public final class Processing {
    private String id;
    private int position;
    private long time;
    private long createTime;
    private int duration;

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
}
