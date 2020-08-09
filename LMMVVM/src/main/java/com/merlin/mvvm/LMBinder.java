package com.merlin.mvvm;

import android.app.Application;
import android.content.Context;
public class LMBinder {
    private static ActivityAutoBinder mActivityBinder;

    public static boolean isAutoBindActivityModelEnable(){
        return null!=mActivityBinder;
    }

    public static boolean autoBindActivityModel(Context context){
        if (null!=mActivityBinder){
            return false;
        }
        context=null!=context?context.getApplicationContext():null;
        if (null!=context&&context instanceof Application){
            ((Application)context).registerActivityLifecycleCallbacks(mActivityBinder=new ActivityAutoBinder());
            return true;
        }
        return false;
    }

}
