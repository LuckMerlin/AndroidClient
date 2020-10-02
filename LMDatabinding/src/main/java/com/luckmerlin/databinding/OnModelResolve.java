package com.luckmerlin.databinding;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnModelResolve extends PublishMethods {
    Object onResolveModel();
}
