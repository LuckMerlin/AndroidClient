package com.merlin.mvvm.broadcast;

import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

public interface OnModelBroadcastResolve {
    List<IntentFilter> onBroadcastResolve(List<IntentFilter> list);
}
