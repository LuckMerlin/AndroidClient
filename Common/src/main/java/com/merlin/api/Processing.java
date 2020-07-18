package com.merlin.api;

import java.util.List;

public class Processing<T,V,M> {
    private String id;
    private T path;
    private int position;
    private long time;
    private long createTime;
    private List<V> data;
    private M terminal;

    public String getId() {
        return id;
    }

    public T getPath() {
        return path;
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


    public M getTerminal() {
        return terminal;
    }


    public List<V> getData() {
        return data;
    }
}
