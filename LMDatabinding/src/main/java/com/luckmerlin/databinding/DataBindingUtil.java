package com.luckmerlin.databinding;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;

import java.lang.reflect.Method;

/**
 * Create LuckMerlin
 * Date 10:58 2020/8/13
 * TODO Uitls
 */
 public class DataBindingUtil implements PublishMethods {

   public Object getBinding(View view){
       Class utilClass=null!=view?util():null;
       try {
           Method method=null!=utilClass?utilClass.getDeclaredMethod("getBinding",View.class):null;
           return null!=method?method.invoke(null,view):null;
       } catch (Exception e) {
          //Do nothing
       }
       return null;
    }

    public static ViewDataBinding inflate(LayoutInflater inflater, int layoutId){
        return null!=inflater?inflate(inflater,layoutId,null,false):null;
    }

    public static ViewDataBinding inflate(LayoutInflater inflater, int layoutId, ViewGroup parent, boolean attachToParent){
        return null!=inflater&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.inflate(inflater,layoutId,parent,attachToParent):null;
    }

    public static ViewDataBinding setContentView(Activity activity, int layoutId){
        return null!=activity&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.setContentView(activity,layoutId):null;
    }

    public static ViewDataBinding setContentView(Activity activity, int layoutId, DataBindingComponent bindingComponent){
        return null!=activity&&checkDataBindingEnable(true)?androidx.databinding.DataBindingUtil.setContentView(activity,layoutId,bindingComponent):null;
    }

    public static final boolean checkDataBindingEnable(boolean print){
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

    Class mUtilClass;

    final Class util(){
        if (null==mUtilClass){
            Class cls=null;
            try {
                cls= Class.forName("androidx.databinding.DataBindingUtil");
            } catch (Exception e) {
                //Do nothing
            }
            mUtilClass=null==cls?void.class:cls;
        }
        Class cls=mUtilClass;
        return null!=cls&&!cls.getName().equals(void.class.getName())?cls:null;
    }

}
