package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityStop extends PublishMethods {
    void onActivityStopped(Activity activity);
}
