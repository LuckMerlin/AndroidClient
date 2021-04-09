package com.luckmerlin.databinding.ui;

import android.view.View;

import com.luckmerlin.databinding.IBinding;

public interface OnSingleTapClick extends IBinding {
    boolean onViewSingleTap(View view, int resId, Object tag);
}
