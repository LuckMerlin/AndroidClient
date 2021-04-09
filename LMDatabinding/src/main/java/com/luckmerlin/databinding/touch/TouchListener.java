package com.luckmerlin.databinding.touch;

import com.luckmerlin.core.proguard.PublishMethods;

public interface TouchListener extends PublishMethods {
    public final static int TOUCH_NONE=0x00;
    public final static int TOUCH_CLICK=0x01;
    public final static int TOUCH_LONG_CLICK=0x02;
}
