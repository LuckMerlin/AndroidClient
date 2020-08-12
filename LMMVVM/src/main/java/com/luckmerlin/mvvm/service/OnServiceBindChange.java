package com.luckmerlin.mvvm.service;

import android.content.ComponentName;
import android.os.IBinder;

public interface OnServiceBindChange {
    void onServiceBindChanged(IBinder binder, ComponentName componentName);
}
