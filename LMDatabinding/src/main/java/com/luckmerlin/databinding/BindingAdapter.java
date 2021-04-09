package com.luckmerlin.databinding;

import android.view.View;

public final class BindingAdapter {

    @androidx.databinding.BindingAdapter("lmBinding")
    public static void setViewValue(View view, IBinding lmBinding) {
        if (null!=view&&null!=lmBinding){
            new BindingInvoker().invoke(view,lmBinding);
        }
    }

}
