package com.luckmerlin.databinding;

import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;

import java.lang.reflect.Method;

public final class MatchBinding implements PublishFields, PublishMethods {
    public final Method mSetMethod;
    public final Method mGetMethod;
    public final ViewDataBinding mViewBinding;
    private Object mCurrent;
    public final Class mType;

    MatchBinding(Method setMethod, Method getMethod, Class type, Object current,ViewDataBinding binding){
        mSetMethod=setMethod;
        mGetMethod=getMethod;
        mType=type;
        mCurrent=current;
        mViewBinding=binding;
    }

    public View getRoot(){
        ViewDataBinding binding= mViewBinding;
        return null!=binding?binding.getRoot():null;
    }

    protected final boolean setCurrent(Object current) {
        this.mCurrent = current;
        return true;
    }

    public final Object getCurrent() {
        return mCurrent;
    }

    @Override

    public String toString() {
        return "MatchBinding{" +
                "mSetMethod=" + mSetMethod +
                ", mGetMethod=" + mGetMethod +
                ", mViewBinding=" + mViewBinding +
                ", mCurrent=" + mCurrent +
                ", mType=" + mType +
                '}';
    }
}
