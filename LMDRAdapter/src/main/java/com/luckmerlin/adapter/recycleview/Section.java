package com.luckmerlin.adapter.recycleview;

import java.util.List;

public final class Section<D> {
    private final String mArg;
    private final int mFrom;
    private final List<D> mData;

    public Section(String arg,int from,List<D> data){
        mArg=arg;
        mFrom=from;
        mData=data;
    }

    public List<D> getData() {
        return mData;
    }

    public String getArg() {
        return mArg;
    }

    public int getFrom() {
        return mFrom;
    }
}
