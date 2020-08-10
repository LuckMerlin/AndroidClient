package com.merlin.mvvm;

import android.content.Context;

/**
 * Create LuckMerlin
 * Date 18:24 2020/8/10
 * TODO
 */
public class ActivityLifeBinder {

    public static boolean bindActivityLife(boolean enable, Context context){
        return LifeNotify.bindActivityLife(enable,context);
    }
}
