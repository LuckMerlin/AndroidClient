package com.luckmerlin.mvvm.broadcast;

import android.content.IntentFilter;

import java.util.List;

public interface OnModelBroadcastResolve {
    List<IntentFilter> onBroadcastResolve(List<IntentFilter> list);
}
