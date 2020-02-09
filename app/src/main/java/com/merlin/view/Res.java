package com.merlin.view;

public final class Res {
    private final Integer mResourceId;
    private final Object mArg;

    public Res(Integer resourceId,Object arg){
        mResourceId=resourceId;
        mArg=arg;
    }

    public Integer getResourceId() {
        return mResourceId;
    }

    public Object getArg() {
        return mArg;
    }
}
