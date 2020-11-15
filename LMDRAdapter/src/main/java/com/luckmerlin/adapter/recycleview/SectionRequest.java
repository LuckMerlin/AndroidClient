package com.luckmerlin.adapter.recycleview;

import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.proguard.PublishMethods;

public final class SectionRequest<T> implements Canceler,PublishMethods {
    private final boolean mNext;
    private final T mArg;
    private final int mFrom;
    Canceler mCanceler;

    SectionRequest(T arg,int from,boolean next){
        mArg=arg;
        mFrom=from;
        mNext=next;
    }

    public int getFrom() {
        return mFrom;
    }

    public T getArg() {
        return mArg;
    }

    public boolean isNext() {
        return mNext;
    }

    public boolean isArgEqual(Object arg) {
        T current=mArg;
        return (null==current||null==arg)||(null!=current&&null!=arg&&current.equals(arg));
    }

    @Override
    public boolean cancel(boolean b, String s) {
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
