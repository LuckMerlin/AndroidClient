package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public final class Touch implements BindingObject,PublishMethods {
    private int mClickDither;
    private final Object mObject;
    private Object mTag;

    public Touch(Object object,Object tag,int clickDither){
        mClickDither=clickDither;
        mObject=object;
        mTag=tag;
    }

    public static Touch touch(Object object){
        return touch(object,0);
    }

    public static Touch touch(Object object,Object tag){
        return touch(object,tag,0);
    }

    public static Touch touch(Object object,int clickDither){
        return new Touch(object,null,clickDither);
    }

    public static Touch touch(Object object,Object tag,int clickDither){
        return new Touch(object,tag,clickDither);
    }

    public Object getObject() {
        return mObject;
    }

    public int getClickDither() {
        return mClickDither;
    }

    public Touch tag(Object tag){
        mTag=tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }
}
