package com.luckmerlin.databinding.ui;

import android.view.View;
import com.luckmerlin.databinding.BindingObject;

public final class TouchBinding implements BindingObject {
    private Object mClick;
    private Object mLongClick;
    private Object mTouch;
    private Integer mClickDither;

    public final TouchBinding longClick(OnViewLongClick callback){
        mLongClick=callback;
        return this;
    }

    public final TouchBinding longClick(boolean enable){
        mLongClick=enable;
        return this;
    }

    public final TouchBinding click(OnViewClick callback){
        mClick=callback;
        return this;
    }

    public final TouchBinding click(boolean enable){
        mClick=enable;
        return this;
    }

    public final TouchBinding clickDither(Integer dither){
        mClickDither=dither;
        return this;
    }

    public final TouchBinding touch(View.OnTouchListener callback){
        mTouch=callback;
        return this;
    }

    public final TouchBinding touch(boolean enable){
        mTouch=enable;
        return this;
    }

    @Override
    public boolean onBind(View view) {
        if (null!=view){
            Object click=mClick;//Click
            Object longClick=mLongClick;//Long click

        }
        return false;
    }
}
