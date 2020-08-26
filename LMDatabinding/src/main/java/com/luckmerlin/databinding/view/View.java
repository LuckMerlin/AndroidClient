package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public final class View implements BindingObject, PublishMethods {
    public final static int ADD=2011;
    public final static int REPLACE=2012;
    private Object mView;
    private Object mParms;
    private int mType=REPLACE;

    private View(Object view,Object parms){
        mView=view;
        mParms=parms;
    }

    public static View v(Object view){
        return v(view,null);
    }

    public static View v(Object view,Object parms){
        return new View(view,parms);
    }

    public View view(Object view){
        mView=view;
        return this;
    }

    public View parms(Object parms){
        mParms=parms;
        return this;
    }

    public View type(int type){
        mType=type;
        return this;
    }

    public Object getView() {
        return mView;
    }

    public Object getParms() {
        return mParms;
    }

    public int getType() {
        return mType;
    }
}
