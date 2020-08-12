package com.luckmerlin.mvvm;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnLowMemory extends PublishMethods {
    boolean onLowMemory();
}
