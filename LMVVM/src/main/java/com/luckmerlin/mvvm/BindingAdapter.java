package com.luckmerlin.mvvm;

import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishMethods;


public final class BindingAdapter implements PublishMethods {

    @androidx.databinding.BindingAdapter("bindModel")
    public static void bindModel(View view, Object modelObj) {
        if (null!=view&&null!=modelObj){
            final ModelBinder binder=new ModelBinder();
            binder.bindFromAdapter(view,modelObj,"While binding adapter called.");
        }
    }
}
