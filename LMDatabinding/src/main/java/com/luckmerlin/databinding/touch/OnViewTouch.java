package com.luckmerlin.databinding.touch;

import android.view.MotionEvent;
import android.view.View;

public interface OnViewTouch {
    boolean onViewTouched(View v, MotionEvent event,Object tag);
}
