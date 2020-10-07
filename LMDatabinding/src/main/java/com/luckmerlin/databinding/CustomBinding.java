package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public interface CustomBinding extends PublishMethods,BindingObject {
    boolean onBind(View view);
}
