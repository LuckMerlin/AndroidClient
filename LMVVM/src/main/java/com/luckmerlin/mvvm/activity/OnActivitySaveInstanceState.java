package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.os.Bundle;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivitySaveInstanceState extends PublishMethods {
    void onActivitySaveInstanceState(Activity activity, Bundle outState);
}
