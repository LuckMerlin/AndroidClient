package com.luckmerlin.databinding.ui;

import android.view.MotionEvent;
import android.view.View;

import com.luckmerlin.databinding.IBinding;

public interface OnViewTouch extends IBinding {
    boolean onViewTouch(View v, int resId,MotionEvent event,Object tag);
}
