package com.merlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luckmerlin.core.debug.Debug;

/**
 * Create LuckMerlin
 * Date 17:50 2020/8/10
 * TODO
 * @deprecated
 */
 final class ActivityLifeNotify implements Application.ActivityLifecycleCallbacks{
    private Model mModel;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
         Debug.D("AAAAAAAAAAAAA ");
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
         Application application=null!=activity?activity.getApplication():null;
         if (null!=application){
            Debug.D("Unregister activity lifecycle callback while activity destroy."+this);
            application.unregisterActivityLifecycleCallbacks(this);
         }
    }
}
