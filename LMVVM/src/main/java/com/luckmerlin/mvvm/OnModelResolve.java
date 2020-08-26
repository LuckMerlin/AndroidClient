package com.luckmerlin.mvvm;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnModelResolve extends PublishMethods {
    Object onResolveModel();
}
