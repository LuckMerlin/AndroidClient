package com.luckmerlin.mvvm.broadcast;

import android.content.Context;
import android.content.Intent;

/**
 * Create LuckMerlin
 * Date 16:38 2020/8/12
 * TODO
 */
public interface OnBroadcastReceive {
    void onBroadcastReceived(Context context, Intent intent);
}
