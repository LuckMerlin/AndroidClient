package com.luckmerlin.mvvm.service;

import android.content.ComponentName;
import android.os.IBinder;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnServiceBindChange extends PublishMethods {
    void onServiceBindChanged(IBinder binder, ComponentName componentName);
}
