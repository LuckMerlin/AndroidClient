package com.luckmerlin.mvvm;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnDetachedToWindow extends PublishMethods {
     void onDetachedToWindow(View v, Model model);
}
