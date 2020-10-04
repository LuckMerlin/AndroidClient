package com.luckmerlin.databinding.touch;

import android.view.MotionEvent;
import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnViewTouch extends TouchListener,PublishMethods, BindingObject {
    boolean onViewTouched(View v, int resId,MotionEvent event,Object tag);
}
