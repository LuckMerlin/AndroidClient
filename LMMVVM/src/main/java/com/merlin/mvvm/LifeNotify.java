package com.merlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.merlin.mvvm.activity.OnActivityCreate;
import com.merlin.mvvm.activity.OnActivityDestroyed;
import com.merlin.mvvm.activity.OnActivityPause;
import com.merlin.mvvm.activity.OnActivityResume;
import com.merlin.mvvm.activity.OnActivitySaveInstanceState;
import com.merlin.mvvm.activity.OnActivityStart;
import com.merlin.mvvm.activity.OnActivityStop;
import com.merlin.mvvm.activity.OnProvideAssistData;
import com.merlin.mvvm.broadcast.OnModelBroadcastResolve;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
final class LifeNotify implements Application.ActivityLifecycleCallbacks,ComponentCallbacks,
        Application.OnProvideAssistDataListener {
    private static LifeNotify mLifeNotify;
    private Map<Object,Model> mModelMaps;
    private Map<Activity,BroadcastReceiver> mModelBroadcast;

    private interface OnIterate{
        boolean onIterated(Object value);
    }

    private interface OnLifeBind{
        void onLifeBind(Application application,boolean enable,LifeNotify notify);
    }

    private LifeNotify(){

    }

    private static boolean bindLife(Context context,boolean enable,OnLifeBind onLifeBind){
        if (null!=onLifeBind){
            context=null!=context?context.getApplicationContext():null;
            if (null!=context&&context instanceof Application){
                LifeNotify lifeNotify=mLifeNotify;
                lifeNotify=enable&&null==lifeNotify?(mLifeNotify=new LifeNotify()):lifeNotify;
                if (null!=lifeNotify){
                    onLifeBind.onLifeBind((Application)context,enable,lifeNotify);
                }
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean bindActivityLife(boolean enable,Context context){
        return bindLife(context,enable,(app,en,life)->{
            if (enable){ app.registerActivityLifecycleCallbacks(life); }else{
                app.unregisterActivityLifecycleCallbacks(life);
            }
        });
    }

    public static synchronized boolean bindComponentLife(boolean enable,Context context){
        return bindLife(context,enable,(app,en,life)->{
            if (enable){ app.registerComponentCallbacks(life); }else{
                app.unregisterComponentCallbacks(life);
            }
        });
    }

    public static synchronized boolean bindProvideAssistData(boolean enable,Context context){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2&&bindLife(context,enable,(app,en,life)->{
            if (enable){
                app.registerOnProvideAssistDataListener(life);
            }else{
                app.unregisterOnProvideAssistDataListener(life);
            }
        });
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        if (null!=activity){
            ModelBinder binder=new ModelBinder();
            Model model=binder.createModel(activity);
            Object actDeclareModel=null!=model&&activity instanceof OnModelLayoutResolve?((OnModelLayoutResolve)activity).onResolveModeLayout():null;
            final View modelView= binder.createModelView(activity,model,actDeclareModel);
            if (null!=modelView&&binder.attachModel(modelView,model)&&addActivityLife(activity,model,"While activity onCreate")){
                activity.setContentView(modelView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                //Collection bind broadcasts
                List<IntentFilter> intents=model instanceof OnModelBroadcastResolve?((OnModelBroadcastResolve)model).onBroadcastResolve(null):null;
                intents=activity instanceof OnModelBroadcastResolve?((OnModelBroadcastResolve)actDeclareModel).onBroadcastResolve(intents):intents;
                if (null!=intents&&intents.size()>0){//Bind each broadcast
                    BroadcastReceiver receiver=null;
                    for (IntentFilter intent:intents) {
                        if (null!=intent) {
                            activity.registerReceiver(null==receiver?receiver=new BroadcastReceiver(){
                                @Override
                                public void onReceive(Context context, Intent intent) {

                                }
                            }:receiver, intent);
                        }
                    }
                    if (null!=receiver){
                        if (!addActivityLife(receiver,null,"While activity onCreate")){
                            activity.unregisterReceiver(receiver);//Fail?Rollback resister
                        }
                    }
                }
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
        Model model=null!=activity?removeModel(activity,activity):null;
        if (null!=model&&model instanceof OnActivityDestroyed) {
            ((OnActivityDestroyed)model).onActivityDestroyed(activity);
        }
    }

    private final Model findActivityModel(Activity activity){
        return null!=activity?getModel(activity):null;
    }

    private boolean addActivityLife(Object object,Model model,String debug){
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

    private Model removeModel(Context context,Object object){
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

    @Override
    public void onProvideAssistData(Activity activity, Bundle data) {
        iterateEach((object)-> null!=object&&object instanceof OnProvideAssistData&&((OnProvideAssistData)object).onProvideAssistData(activity,data));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        iterateEach((object)-> null!=object&&object instanceof OnConfigurationChange&&((OnConfigurationChange)object).onConfigurationChanged(newConfig));
    }

    @Override
    public void onLowMemory() {
        iterateEach((object)-> null!=object&&object instanceof OnLowMemory&&((OnLowMemory)object).onLowMemory());
    }

    private void iterateEach(OnIterate iterate){
        Map<Object,Model>modelMap= mModelMaps;
        Set<Object> set=null!=modelMap?modelMap.keySet():null;
        if (null!=iterate&&null!=set&&set.size()>0){
            for (Object object:set) {
                if (null!=object&&!iterate.onIterated(object)){
                    Model model=modelMap.get(object);
                    if (null==model||!iterate.onIterated(model)){
                        continue;
                    }
                }
                break;
            }
        }
    }
}
