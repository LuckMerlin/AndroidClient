package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public class Image implements BindingObject,PublishMethods {
    private Object mSrc;
    private Object mBackground;

    public Image(){
        this(null,null);
    }

    public Image(Object image,Object background){
        mBackground=background;
        mSrc=image;
    }

    public static Image image(Object image){
        return image(image,null);
    }

    public static Image image(Object image,Object background){
        return new Image(image,background);
    }

    public Image src(Object image){
        mSrc=image;
        return this;
    }

    public Image background(Object object){
        mBackground=object;
        return this;
    }

    public Object getBackground() {
        return mBackground;
    }

    public Object getSrc() {
        return mSrc;
    }
}
