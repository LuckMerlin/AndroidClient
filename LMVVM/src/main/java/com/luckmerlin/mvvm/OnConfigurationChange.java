package com.luckmerlin.mvvm;

import android.content.res.Configuration;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnConfigurationChange {
    boolean onConfigurationChanged(Configuration newConfig);
}
