package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.content.Intent;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityIntentChange extends PublishMethods {
   void onActivityIntentChanged(Activity activity, Intent intent);
}
