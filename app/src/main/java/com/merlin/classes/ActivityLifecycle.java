package com.merlin.classes;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;
import java.util.WeakHashMap;

public final class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private Callback[] mCallbacks;
    private final WeakHashMap<Activity,Long> mRunningActivity=new WeakHashMap(1);

    public interface Callback{

    }

    public interface OnActivityCreate extends Callback{
        void onActivityCreated(Activity activity,Bundle savedInstanceState);
    }

    public interface OnActivityDestroy extends Callback{
        void onActivityDestroy(Activity activity);
    }

    public ActivityLifecycle(Callback ...callbacks){
        mCallbacks=callbacks;
    }

    public Activity getTopActivity(){
        WeakHashMap<Activity,Long> running=mRunningActivity;
        Set<Activity> set=null!=running?running.keySet():null;
        if (null!=set){
            Long latestTime=null;
            Activity latestActivity=null;
            for(Activity activity:set){
                Long time=null!=activity?running.get(activity):null;
                if (null!=time&&(null==latestTime||(latestTime<time))){
                    latestActivity=activity;
                    latestTime=time;
                }
            }
            return latestActivity;
        }
        return null;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (null!=activity) {
            mRunningActivity.put(activity, System.currentTimeMillis());
            Callback[] callbacks = mCallbacks;
            if (null != callbacks && callbacks.length > 0) {
                for (Callback callback : callbacks) {
                    if (null != callback && callback instanceof OnActivityCreate) {
                        ((OnActivityCreate) callback).onActivityCreated(activity, savedInstanceState);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (null!=activity) {
            mRunningActivity.put(activity, System.currentTimeMillis());
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPostDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPostStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPostStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityPreDestroyed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPrePaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityPreStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPreStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (null!=activity) {
            mRunningActivity.remove(activity);
            Callback[] callbacks = mCallbacks;
            if (null != callbacks && callbacks.length > 0) {
                for (Callback callback : callbacks) {
                    if (null != callback && callback instanceof OnActivityDestroy) {
                        ((OnActivityDestroy) callback).onActivityDestroy(activity);
                    }
                }
            }
        }
    }

}
