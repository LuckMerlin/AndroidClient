package com.luckmerlin.databinding.ui;

import android.view.View;

import com.luckmerlin.databinding.IBinding;

public interface OnViewClick extends IBinding {
    boolean onViewClick(View view,int resId,int count,Object tag);
}
