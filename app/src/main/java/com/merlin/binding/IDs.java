package com.merlin.binding;

import java.lang.ref.WeakReference;

public class IDs {
    private final int mResourceId;
    private final WeakReference<Object> mArg;

    public IDs(int resourceId,Object arg){
        mResourceId=resourceId;
        mArg=null!=arg?new WeakReference<>(arg):null;
    }


    public Object getArg() {
        WeakReference<Object> reference=mArg;
        return null!=reference?reference.get():null;
    }

    public int getResourceId() {
        return mResourceId;
    }
}
