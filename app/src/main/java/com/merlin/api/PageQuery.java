package com.merlin.api;

import androidx.annotation.Nullable;

public class PageQuery<T> {
    private final int mPage;
    private final int mLimit;
    private final T mArg;

    public PageQuery(){
        this(null);
    }

    public PageQuery(T arg){
        this(arg,0,10);
    }

    public PageQuery(T arg,int page,int limit){
        mArg=arg;
        mPage=page;
        mLimit=limit;
    }

    public final int getPage() {
        return mPage;
    }

    public final int getLimit() {
        return mLimit;
    }

    public final T getArg() {
        return mArg;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof PageQuery){
            PageQuery query=((PageQuery)obj);
            return query.mLimit==mLimit&&query.mPage==mPage&&
                    ((null==query.mArg&&null==mArg)||(null!=query.mArg&&null!=mArg&&query.mArg.equals(mArg)));
        }
        return super.equals(obj);
    }
}
