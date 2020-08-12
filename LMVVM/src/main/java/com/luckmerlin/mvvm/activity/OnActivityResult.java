package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.content.Intent;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityResult extends PublishMethods {
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}
