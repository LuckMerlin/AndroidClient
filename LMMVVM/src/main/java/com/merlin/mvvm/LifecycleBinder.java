package com.merlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

class LifecycleBinder implements Application.ActivityLifecycleCallbacks{
    private final Model mModel;
    private final WeakReference<Activity> mLifeObj;

    public LifecycleBinder(Activity context,Model model){
        mLifeObj=null!=context?new WeakReference<>(context):null;
        mModel=model;
    }

    public boolean bind(){
        WeakReference<Activity> lifeObj=mLifeObj;
        Activity activity=null!=lifeObj?lifeObj.get():null;
        if (null!=activity){
//            activity.unregisterActivityLifecycleCallbacks(this);
        }
        return false;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
