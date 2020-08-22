package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public final class Touch implements BindingObject,PublishMethods {
    public final static int CLICK=2008;
    public final static int LONG_CLICK=2018;
    private final Object mObject;
    private Object mTag;

    public Touch(Object object,Object tag){
        mObject=object;
        mTag=tag;
    }

    public static Touch touch(Object object){
        return new Touch(object,null);
    }

    public static Touch touch(Object object,Object tag){
        return new Touch(object,tag);
    }

    public Object getObject() {
        return mObject;
    }

    public Touch tag(Object tag){
        mTag=tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }
}
