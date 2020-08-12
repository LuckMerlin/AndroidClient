package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityStart extends PublishMethods {
    void onActivityStarted(Activity activity);
}
