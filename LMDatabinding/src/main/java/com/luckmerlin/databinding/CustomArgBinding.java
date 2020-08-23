package com.luckmerlin.databinding;

public abstract class CustomArgBinding implements CustomBinding {
    private Object mArg;

    public final CustomArgBinding setArg(Object arg){
        mArg=arg;
        return this;
    }

    public final Object getArg(){
        return mArg;
    }
}
