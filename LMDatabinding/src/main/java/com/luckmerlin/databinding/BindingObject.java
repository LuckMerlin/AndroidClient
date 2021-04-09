package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public interface BindingObject extends PublishMethods, IBinding {
    boolean onBind(View view);
}
