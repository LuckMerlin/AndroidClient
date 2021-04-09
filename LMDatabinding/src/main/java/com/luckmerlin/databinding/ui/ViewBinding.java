package com.luckmerlin.databinding.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.luckmerlin.databinding.BindingObject;

public class ViewBinding implements BindingObject {
    private Object mBackground;
    private Object mText;
    private Object mHint;
    private Object mImage;

    public final ViewBinding text(CharSequence text){
        mText=text;
        return this;
    }

    public final ViewBinding text(Integer resId){
        mText=null!=resId?resId:null;
        return this;
    }

    public final ViewBinding hint(CharSequence text){
        mText=text;
        return this;
    }

    public final ViewBinding hint(Integer resId){
        mText=null!=resId?resId:null;
        return this;
    }

    public Object getHint() {
        return mHint;
    }

    public final Object getText() {
        return mText;
    }

    public final ViewBinding background(Drawable drawable){
        mBackground=drawable;
        return this;
    }

    public final ViewBinding background(Bitmap bitmap){
        mBackground=bitmap;
        return this;
    }

    public final ViewBinding background(Integer resId){
        mBackground=null!=resId?resId:null;
        return this;
    }

    public final ViewBinding background(){
        mBackground=null;
        return this;
    }

    public final ViewBinding image(Drawable drawable){
        mImage=drawable;
        return this;
    }

    public final ViewBinding image(Bitmap bitmap){
        mImage=bitmap;
        return this;
    }

    public final ViewBinding image(Integer resId){
        mImage=null!=resId?resId:null;
        return this;
    }

    public final ViewBinding image(){
        mImage=null;
        return this;
    }

    public Object getBackground() {
        return mBackground;
    }

    @Override
    public boolean onBind(View view) {
        return false;
    }
}
