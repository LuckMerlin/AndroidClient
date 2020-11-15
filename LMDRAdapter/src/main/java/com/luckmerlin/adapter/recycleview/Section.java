package com.luckmerlin.adapter.recycleview;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.List;

public final class Section<A,D> implements PublishMethods {
    private final A mArg;
    private final int mFrom;
    private final List<D> mData;
    private final int mLength;

    public Section(A arg,int from,List<D> data,int length){
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

    public int getFrom() {
        return mFrom;
    }

    public int getLength() {
        return mLength;
    }
}
