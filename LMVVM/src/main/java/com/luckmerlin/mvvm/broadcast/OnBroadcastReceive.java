package com.luckmerlin.mvvm.broadcast;

import android.content.Context;
import android.content.Intent;

import com.luckmerlin.core.proguard.PublishMethods;

/**
 * Create LuckMerlin
 * Date 16:38 2020/8/12
 * TODO
 */
public interface OnBroadcastReceive extends PublishMethods {
    void onBroadcastReceived(Context context, Intent intent);
}
