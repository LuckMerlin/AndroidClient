package com.luckmerlin.databinding;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.luckmerlin.core.proguard.PublishMethods;

public class ActivityRootFinder implements PublishMethods {

    public final View getActivityFirstRoot(Activity activity){
        View content=null!=activity?activity.findViewById(android.R.id.content):null;
        return null!=content&&content instanceof ViewGroup &&((ViewGroup)content).
                getChildCount()>0?((ViewGroup)content).getChildAt(0):null;
    }

}
