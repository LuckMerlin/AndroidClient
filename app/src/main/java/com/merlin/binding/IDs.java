package com.merlin.binding;

public class IDs {
    private final int mResourceId;
        private final Object mArg;

    public IDs(int resourceId,Object arg){
        mResourceId=resourceId;
        mArg=arg;
    }

    public Object getArg() {
        return mArg;
    }

    public int getResourceId() {
        return mResourceId;
    }
}
