package com.luckmerlin.mvvm;

import android.app.Activity;
import android.content.Context;

import com.luckmerlin.core.proguard.PublishMethods;

/**
 * Create LuckMerlin
 * Date 10:54 2020/8/13
 * TODO
 */
public final class LifeBinder implements PublishMethods {

    private LifeBinder(){

    }

    public static boolean bindActivityLife(boolean enable, Activity activity){
        return LifeBinderImpl.bindActivityLife(enable, activity);
    }

    public static boolean bindComponentLife(boolean enable, Context context){
        return LifeBinderImpl.bindComponentLife(enable, context);
    }
}
