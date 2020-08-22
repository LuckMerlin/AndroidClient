package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public class BindingAdapter implements PublishMethods {

    @androidx.databinding.BindingAdapter("lmBinding")
    public static void setViewValue(View view, BindingObject lmBinding) {
        if (null!=view&&null!=lmBinding){
            new ViewBinding().bind(view,lmBinding);
        }
    }

}
