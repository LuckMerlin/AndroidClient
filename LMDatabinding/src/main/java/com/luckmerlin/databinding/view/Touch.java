package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;
import com.luckmerlin.databinding.touch.TouchListener;

import java.lang.ref.WeakReference;

public final class Touch implements BindingObject,PublishMethods, PublishFields {
    public final static int NONE=0; //0x 0000 0000
    public final static int CLICK=1; //0x 0000 0001
    public final static int LONG_CLICK=2; // 0000 0010
    public final static int TOUCH=4; // 0000 0100
    private int mClickDither;
    private final Object mObject;
    private Integer mDispatch;
    private Object mTag;
    private WeakReference<TouchListener> mListener;

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

    public static Touch dither(Integer dispatch,Object tag){
        return dispatch(dispatch,tag).dither();
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

    public static Touch imageRes(Object image,Object tag,Integer dispatch){
        return imageRes(image,null,tag,dispatch,null,-1);
    }

    public static Touch imageRes(Object image,Object background,Object tag,Integer dispatch,Object object,int clickDither){
        return new Touch(object,null!=image||null!=background?new ImageResTag(image,background,tag):null,clickDither).enableDispatch(dispatch);
    }

    public static Touch textRes(Object textResId, Object tag,Integer dispatch){
        return textRes(textResId,null,tag,dispatch,null,-1);
    }

    public static Touch textRes(Object textResId, Object color,Object tag,Integer dispatch,Object object,int clickDither){
        return new Touch(object,null!=textResId||null!=color?new TextResTag(textResId,color,tag):
                null,clickDither).enableDispatch(dispatch);
    }

    public Touch listener(TouchListener runnable){
        WeakReference<TouchListener> reference=mListener;
        mListener=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=runnable){
            mListener=new WeakReference<>(runnable);
        }
        return this;
    }

    public Touch dither(){
        return dither(200);
    }

    public Touch dither(int dither){
        mClickDither=dither;
        return this;
    }

    public final Touch enableDispatch(Integer dispatch){
        mDispatch=dispatch;
        return this;
    }

    public TouchListener getListener() {
        WeakReference<TouchListener> reference=mListener;
        return null!=reference?reference.get():null;
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
