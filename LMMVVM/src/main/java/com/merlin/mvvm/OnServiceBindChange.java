package com.merlin.mvvm;

import android.content.ComponentName;
import android.os.Binder;

public interface OnServiceBindChange {
    void onServiceBindChanged(Binder binder, ComponentName componentName);
}
