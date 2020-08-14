package com.luckmerlin.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.DataBinding;
import com.luckmerlin.core.debug.Debug;

/**
 * Create LuckMerlin
 * Date 10:58 2020/8/13
 * TODO
 */
 final class DataBindingUtil {

    static ViewDataBinding getBinding(View view){
        return null!=view&&new DataBinding().checkDataBindingEnable(false)?androidx.databinding.DataBindingUtil.getBinding(view):null;
    }

    static ViewDataBinding inflate(LayoutInflater inflater, int layoutId, @Nullable ViewGroup parent, boolean attachToParent){
        return null!=inflater&&new DataBinding().checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.inflate(inflater,layoutId,parent,attachToParent):null;
    }

}
