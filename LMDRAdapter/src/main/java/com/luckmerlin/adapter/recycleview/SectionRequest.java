package com.luckmerlin.adapter.recycleview;

public final class SectionRequest<T> {
    private final T mArg;
    private final int mPage;
    private final int mLimit;

    public SectionRequest(T arg,int page,int limit){
        mArg=arg;
        mPage=page;
        mLimit=limit;
    }

    public int getLimit() {
        return mLimit;
    }

    public int getPage() {
        return mPage;
    }

    public T getArg() {
        return mArg;
    }

}
