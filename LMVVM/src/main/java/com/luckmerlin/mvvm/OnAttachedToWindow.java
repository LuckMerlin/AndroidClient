package com.luckmerlin.mvvm;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnAttachedToWindow extends PublishMethods {
     void onAttachedToWindow(View v, Model model);
}
