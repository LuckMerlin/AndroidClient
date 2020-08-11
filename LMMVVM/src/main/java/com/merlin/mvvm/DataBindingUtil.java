package com.merlin.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;

/**
 * Create LuckMerlin
 * Date 12:00 2020/8/10
 * TODO
 */
 class DataBindingUtil {

  static ViewDataBinding getBinding(View view){
      return null!=view&&checkDataBindingEnable(false)?androidx.databinding.DataBindingUtil.getBinding(view):null;
  }

  static ViewDataBinding inflate(LayoutInflater inflater, int layoutId, @Nullable ViewGroup parent, boolean attachToParent){
      return null!=inflater&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.inflate(inflater,layoutId,parent,attachToParent):null;
  }

  final static boolean checkDataBindingEnable(boolean print){
      try {
          Class.forName("androidx.databinding.DataBinderMapperImpl");
          return true;
      }catch (ClassNotFoundException e) {
          if (print) {
              Debug.W("Maybe you need configure databinding enable in build.gradle for module as follow.\n\nandroid {\n  dataBinding{\n     enabled true\n  }\n}\n\n");
          }
      }
      return false;
  }
}
