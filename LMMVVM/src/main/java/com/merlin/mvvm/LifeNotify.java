package com.merlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.merlin.mvvm.activity.OnActivityCreate;
import com.merlin.mvvm.activity.OnActivityDestroyed;
import com.merlin.mvvm.activity.OnActivityPause;
import com.merlin.mvvm.activity.OnActivityResume;
import com.merlin.mvvm.activity.OnActivitySaveInstanceState;
import com.merlin.mvvm.activity.OnActivityStart;
import com.merlin.mvvm.activity.OnActivityStop;

import java.util.Map;
import java.util.WeakHashMap;

final class LifeNotify implements Application.ActivityLifecycleCallbacks {
    private static LifeNotify mLifeNotify;
    private Map<Object,Model> mModelMaps;

    private LifeNotify(){

    }

    public static synchronized boolean bindActivityLife(boolean enable,Context context){
        context=null!=context?context.getApplicationContext():null;
        if (null!=context&&context instanceof Application){
            LifeNotify lifeNotify=mLifeNotify;
            lifeNotify=enable&&null==lifeNotify?(mLifeNotify=new LifeNotify()):lifeNotify;
            if (null!=lifeNotify){
                ((Application)context).unregisterActivityLifecycleCallbacks(lifeNotify);
                if (enable){
                    ((Application)context).registerActivityLifecycleCallbacks(lifeNotify);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (null!=activity){
            ModelBinder binder=new ModelBinder();
            Model model=binder.createModel(activity);
            Object actDeclareModel=null!=model&&activity instanceof OnModelLayoutResolve?((OnModelLayoutResolve)activity).onResolveModeLayout():null;
            final View modelView= binder.createModelView(activity,model,actDeclareModel);
            if (null!=modelView&&binder.attachModel(modelView,model)&&addModel(activity,model,"While activity onCreate")){
                activity.setContentView(modelView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }
            if (null!=model&&model instanceof OnActivityCreate) {
                ((OnActivityCreate)model).onActivityCreated(activity,savedInstanceState);
            }
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityStart) {
            ((OnActivityStart)model).onActivityStarted(activity);
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityResume) {
            ((OnActivityResume)model).onActivityResume(activity);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityPause) {
            ((OnActivityPause)model).onActivityPaused(activity);
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityStop) {
            ((OnActivityStop)model).onActivityStopped(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivitySaveInstanceState) {
            ((OnActivitySaveInstanceState)model).onActivitySaveInstanceState(activity,outState);
        }
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Model model=null!=activity?removeModel(activity):null;
        if (null!=model&&model instanceof OnActivityDestroyed) {
            ((OnActivityDestroyed)model).onActivityDestroyed(activity);
        }
    }

    private final Model findActivityModel(Activity activity){
        return null!=activity?getModel(activity):null;
    }

    private boolean addModel(Object object,Model model,String debug){
        if (null!=object&&null!=model){
            Map<Object,Model> modelMap=mModelMaps;
            modelMap=null!=modelMap?modelMap:(mModelMaps=new WeakHashMap<>());
            modelMap.put(object,model);
            return true;
        }
        return false;
    }

    private Model getModel(Object object){
        Map<Object,Model> modelMap=null!=object?mModelMaps:null;
        return null!=modelMap?modelMap.get(object):null;
    }

    private Model removeModel(Object object){
        Map<Object,Model> modelMap=null!=object?mModelMaps:null;
        Model removed=null;
        if (null!=modelMap){
            removed=modelMap.remove(object);
        }
        if (null!=modelMap&&modelMap.size()<=0){
            mModelMaps=null;
        }
        return removed;
    }

}
