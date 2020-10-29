package com.luckmerlin.mvvm;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.Model;

public interface OnAttachedToWindow extends PublishMethods {
     void onAttachedToWindow(View v, Model model);
}
