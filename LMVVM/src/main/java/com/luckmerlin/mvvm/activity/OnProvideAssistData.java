package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.os.Bundle;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnProvideAssistData extends PublishMethods {
    boolean onProvideAssistData(Activity activity, Bundle data);
}
