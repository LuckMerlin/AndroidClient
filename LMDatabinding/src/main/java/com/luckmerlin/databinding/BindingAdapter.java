package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.view.ViewValuer;

public class BindingAdapter implements PublishMethods {

    @androidx.databinding.BindingAdapter("viewValue")
    public static void setViewValue(View view, Object viewValue) {
        if (null!=view&&null!=viewValue){
            new ViewValuer().set(view,viewValue);
        }
    }

}
