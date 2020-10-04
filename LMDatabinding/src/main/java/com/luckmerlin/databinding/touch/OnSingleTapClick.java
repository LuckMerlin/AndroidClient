package com.luckmerlin.databinding.touch;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnSingleTapClick extends TouchListener, PublishMethods, BindingObject {
    boolean onViewSingleTap(View view, int resId, Object tag);
}
