package com.luckmerlin.databinding.ui;

import android.view.View;

import com.luckmerlin.databinding.IBinding;

public interface OnViewLongClick extends IBinding {
    boolean onViewLongClick(View v,int resId,Object tag);
}
