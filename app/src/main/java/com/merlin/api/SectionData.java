package com.merlin.api;

import java.util.List;

public class SectionData<T> {
    private int length=-1;
    private int from;
    private int to;
    private List<T> data;

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setTo(int to) {
        this.to = to;
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
