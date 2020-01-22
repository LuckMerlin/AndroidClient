package com.merlin.api;

import java.util.List;

public class Page<T> {
    private int page;
    private int limit;
    private List<T> data;

//    public Page(int page,int limit){
//        this.page=page;
//        this.limit=limit;
//    }

    public final void setLimit(int limit) {
        this.limit = limit;
    }

    public final int getLimit() {
        return limit;
    }

    public final int getPage() {
        return page;
    }

    public final void setPage(int page) {
        this.page = page;
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
}