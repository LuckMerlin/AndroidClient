package com.luckmerlin.databinding.view;

import android.view.ViewGroup;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public final class View implements BindingObject, PublishMethods {
    public final static int ADD=2011;
    public final static int REPLACE=2012;
    public final static int SET=2013;
    private Object mView;
    private int mPosition;
    private ViewGroup.LayoutParams mParms;
    private int mType=REPLACE;

    private View(Object view,int type,ViewGroup.LayoutParams parms){
        mView=view;
        mType=type;
        mParms=parms;
    }

    public static View view(Object view,int type){
        return view(view,type,null);
    }

    public static View view(Object view,int type,ViewGroup.LayoutParams parms){
        return new View(view,type,parms);
    }

    public View setView(Object view){
        mView=view;
        return this;
    }

    public View position(int position){
        mPosition=position;
        return this;
    }

    public View parms(ViewGroup.LayoutParams parms){
        mParms=parms;
        return this;
    }

    public View type(int type){
        mType=type;
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

    public Object getView() {
        return mView;
    }

    public final ViewGroup.LayoutParams getParms() {
        return mParms;
    }

    public int getType() {
        return mType;
    }
}
