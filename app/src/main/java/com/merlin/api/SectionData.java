package com.merlin.api;

import java.util.List;

public class SectionData<T> {
    private int length=-1;
    private int from;
    private List<T> data;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        List<T>  list=from>=0?data:null;
        return from+(null!=list?list.size():0);
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public final void setData(List<T> data) {
        this.data = data;
    }

    public final List<T> getData() {
        return data;
    }

    public final int size(){
        return null!=data?data.size():0;
    }

    public int getLength() {
        return length;
    }
}
