package com.luckmerlin.adapter.recycleview;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.proguard.PublishMethods;

public final class SectionRequest<T> implements Canceler,PublishMethods {
    private final boolean mNext;
    private final T mArg;
    private final long mFrom;
    private final long mLimit;
    Canceler mCanceler;

    public SectionRequest(T arg,long from,boolean next){
        this(arg,from,60,next);
    }

    public SectionRequest(T arg,long from,int limit,boolean next){
        mArg=arg;
        mLimit=limit;
        mFrom=from;
        mNext=next;
    }

    public final long getFrom() {
        return mFrom;
    }

    public final T getArg() {
        return mArg;
    }

    public final long getLimit() {
        return mLimit;
    }

    public final boolean isNext() {
        return mNext;
    }

    public final boolean isArgEqual(Object arg) {
        T current=mArg;
        return (null==current||null==arg)||(null!=current&&null!=arg&&current.equals(arg));
    }

    @Override
    public final boolean cancel(boolean b, String s) {
        Canceler canceler=mCanceler;
        return null!=canceler&&canceler.cancel(b,s);
    }

    @Override
    public boolean equals(Object o) {
        if (null!=o&& o instanceof SectionRequest){
            SectionRequest request=(SectionRequest)o;
            return request.mNext==mNext;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SectionRequest{" +
                "mNext=" + mNext +
                ", mArg=" + mArg +
                ", mFrom=" + mFrom +
                ", mCanceler=" + mCanceler +
                '}';
    }
}
