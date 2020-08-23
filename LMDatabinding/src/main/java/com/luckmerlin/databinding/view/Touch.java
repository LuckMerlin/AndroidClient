package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public final class Touch implements BindingObject,PublishMethods, PublishFields {
    public final static int NONE=0; //0x 0000 0000
    public final static int CLICK=1; //0x 0000 0001
    public final static int LONG_CLICK=2; // 0000 0010
    public final static int TOUCH=4; // 0000 0100
    private int mClickDither;
    private final Object mObject;
    private Integer mDispatch;
    private Object mTag;

    Touch(Object object,Object tag,int clickDither){
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

    public static Touch dispatch(Integer dispatch){
        return dispatch(dispatch,null);
    }

    public static Touch dispatch(Integer dispatch,Object tag){
        return dispatch(dispatch,null,tag);
    }

    public static Touch dispatch(Integer dispatch,Object object,Object tag){
        return dispatch(dispatch,object,tag,0);
    }

    public static Touch dispatch(Integer dispatch,Object object,Object tag,int clickDither){
        return new Touch(object,tag,clickDither).enableDispatch(dispatch);
    }

    public final Touch enableDispatch(Integer dispatch){
        mDispatch=dispatch;
        return this;
    }

    public Object getObject() {
        return mObject;
    }

    public Integer isDispatchEnable() {
        return mDispatch;
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
