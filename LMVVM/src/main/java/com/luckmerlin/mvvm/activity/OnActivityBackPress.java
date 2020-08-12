package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityBackPress extends PublishMethods {
    boolean onActivityBackPressed(Activity activity);
}
