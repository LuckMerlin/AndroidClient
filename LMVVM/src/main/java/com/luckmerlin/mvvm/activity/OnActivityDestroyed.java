package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityDestroyed extends PublishMethods {
    void onActivityDestroyed(Activity activity);
}
