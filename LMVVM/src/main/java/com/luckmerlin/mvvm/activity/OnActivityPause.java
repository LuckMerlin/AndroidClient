package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityPause extends PublishMethods {
    void onActivityPaused(Activity activity);
}
