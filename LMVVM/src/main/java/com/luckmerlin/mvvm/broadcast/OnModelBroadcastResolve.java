package com.luckmerlin.mvvm.broadcast;

import android.content.IntentFilter;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.List;

public interface OnModelBroadcastResolve extends PublishMethods {
    List<IntentFilter> onBroadcastResolve(List<IntentFilter> list);
}
