//package com.luckmerlin.core.view;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.luckmerlin.core.debug.Debug;
//import com.luckmerlin.core.proguard.PublishMethods;
//
///**
// * Create LuckMerlin
// * Date 10:58 2020/8/13
// * TODO Uitls
// */
// public final class DataBindingUtil implements PublishMethods {
//
//   public static ViewDataBinding getBinding(View view){
//        return null!=view&&checkDataBindingEnable(false)?androidx.databinding.DataBindingUtil.getBinding(view):null;
//    }
//
//    public static ViewDataBinding inflate(LayoutInflater inflater, int layoutId){
//        return null!=inflater?inflate(inflater,layoutId,null,false):null;
//    }
//
//    public static ViewDataBinding inflate(LayoutInflater inflater, int layoutId, ViewGroup parent, boolean attachToParent){
//        return null!=inflater&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.inflate(inflater,layoutId,parent,attachToParent):null;
//    }
//
//    public static ViewDataBinding setContentView(Activity activity, int layoutId){
//        return null!=activity&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.setContentView(activity,layoutId):null;
//    }
//
//    public static ViewDataBinding setContentView(Activity activity, int layoutId, DataBindingComponent bindingComponent){
//        return null!=activity&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.setContentView(activity,layoutId,bindingComponent):null;
//    }
//
//    public static final boolean checkDataBindingEnable(boolean print){
//        try {
//            Class.forName("androidx.databinding.DataBinderMapperImpl");
//            return true;
//        }catch (ClassNotFoundException e) {
//            if (print) {
//                Debug.W("Maybe you need configure databinding enable in build.gradle for module as follow.\n\nandroid {\n  dataBinding{\n     enabled true\n  }\n}\n\n");
//            }
//        }
//        return false;
//    }
//
//}
