package com.merlin.model;

import android.content.ComponentName;
import android.os.IBinder;

public interface OnServiceBindChange {
    void onServiceBindChanged(ComponentName name, IBinder service);
}
