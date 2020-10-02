package com.luckmerlin.mvvm;

import android.content.Context;

import com.luckmerlin.core.proguard.PublishMethods;

/**
 * Create LuckMerlin
 * Date 10:54 2020/8/13
 * TODO
 */
public final class ModelLifeBinder implements PublishMethods{
    private ModelLifeBinder(){

    }

    public static boolean bindActivityLife(boolean enable, Context context){
        return new ModelLifeBinderImpl().bindActivityLife(enable, context);
    }

//    public static boolean bindComponentLife(boolean enable, Context context){
//        return new LifeBinderImpl().bindComponentLife(enable, context);
//    }
}
