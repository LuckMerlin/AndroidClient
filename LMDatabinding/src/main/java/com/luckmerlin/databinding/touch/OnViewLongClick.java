package com.luckmerlin.databinding.touch;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnViewLongClick extends PublishMethods, BindingObject {
    boolean onViewLongClick(View v,Object tag);
}
