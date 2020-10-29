package com.luckmerlin.mvvm;

import android.content.Context;

import com.luckmerlin.core.proguard.PublishMethods;

/**
 * Create LuckMerlin
 * Date 10:54 2020/8/13
 * TODO
 */
public final class ModelLifeBinder implements PublishMethods{
    private final static ModelLifeBinderImpl mInstance=new ModelLifeBinderImpl();
    private ModelLifeBinder(){

    }

    public static boolean bindActivityLife(boolean enable, Context context){
        return mInstance.bindActivityLife(enable, context);
    }
}
