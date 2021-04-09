package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

public class Image implements IBinding,PublishMethods {
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

    public final Image src(Object image){
        mSrc=image;
        return this;
    }

    public final Image background(Object object){
        mBackground=object;
        return this;
    }

    public final Object getBackground() {
        return mBackground;
    }

    public final Object getSrc() {
        return mSrc;
    }
}
