package com.merlin.api;

import java.util.List;

public final class SectionData<T> {
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
