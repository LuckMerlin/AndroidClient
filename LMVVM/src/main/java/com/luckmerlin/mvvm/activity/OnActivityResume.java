package com.luckmerlin.mvvm.activity;

import android.app.Activity;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityResume extends PublishMethods {
    void onActivityResume(Activity activity);
}
