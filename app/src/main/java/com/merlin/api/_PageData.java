package com.merlin.api;

import java.util.List;

/**
 *
 * @deprecated
 */
public class _PageData<T> {
    private int length;
    private int page;
    private int limit;
    private List<T> data;

    public final int getLimit() {
        return limit;
    }

    public final int getPage() {
        return page;
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
