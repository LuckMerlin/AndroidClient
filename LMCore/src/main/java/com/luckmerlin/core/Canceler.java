package com.luckmerlin.core;

import com.luckmerlin.core.proguard.PublishMethods;

public interface Canceler extends PublishMethods {
    boolean cancel(boolean cancel, String debug);
}
