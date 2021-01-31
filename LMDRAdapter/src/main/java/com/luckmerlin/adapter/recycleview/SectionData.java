package com.luckmerlin.adapter.recycleview;

import java.util.List;

public final class SectionData<A,D> implements Section<A,D> {
    private final A mArg;
    private final int mFrom;
    private final List<D> mData;
    private final int mLength;

    public SectionData(A arg, int from, List<D> data, int length){
        mArg=arg;
        mFrom=from;
        mData=data;
        mLength=length;
    }

    public List<D> getData() {
        return mData;
    }

    public A getArg() {
        return mArg;
    }

    public long getFrom() {
        return mFrom;
    }

    public long getTotal() {
        return mLength;
    }
}
