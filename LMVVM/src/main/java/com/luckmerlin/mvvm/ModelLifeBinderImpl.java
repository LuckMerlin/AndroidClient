package com.luckmerlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.ActivityRootFinder;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.ModelClassFinder;
import com.luckmerlin.mvvm.activity.OnActivityCreate;
import com.luckmerlin.mvvm.activity.OnActivityDestroyed;
import com.luckmerlin.mvvm.activity.OnActivityPause;
import com.luckmerlin.mvvm.activity.OnActivityResume;
import com.luckmerlin.mvvm.activity.OnActivitySaveInstanceState;
import com.luckmerlin.mvvm.activity.OnActivityStart;
import com.luckmerlin.mvvm.activity.OnActivityStop;
import com.luckmerlin.mvvm.broadcast.HandlerBroadcastReceiver;
import com.luckmerlin.mvvm.broadcast.OnBroadcastReceive;
import com.luckmerlin.mvvm.broadcast.OnModelBroadcastResolve;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ModelLifeBinderImpl implements Application.ActivityLifecycleCallbacks{
    private static Map<Object,Activity> mActivityRegister;
    private final ActivityRootFinder mActRootFinder=new ActivityRootFinder();
    private final ModelClassFinder mModelFinder=new ModelClassFinder();

    private interface OnLifeBind{
        void onLifeBind(Application application, boolean enable);
    }

    private Model findActivityModel(Activity activity){
       View activityRoot= null!=activity?mActRootFinder.getActivityFirstRoot(activity):null;
       if (null!=activityRoot&&DataBindingUtil.checkDataBindingEnable(false)){
           ViewDataBinding binding=DataBindingUtil.getBinding(activityRoot);
           MatchBinding matchBinding=null!=binding?mModelFinder.findModel(binding,null):null;
           Object current=null!=matchBinding?matchBinding.getCurrent():null;
           return null!=current&&current instanceof Model?((Model)current):null;
       }
        return null;
    }

    @Override
    public void onActivityCreated(final Activity activity, Bundle savedInstanceState) {
        if (null!=activity){
            Model activityModel=null;
            if (DataBindingUtil.checkDataBindingEnable(false)){
                ModelBinder modelBinder=new ModelBinder();
                MatchBinding matchBinding=modelBinder.bindModelForObject(activity,activity,"While activity onCreate.");
                Object currentModel=null!=matchBinding?matchBinding.getCurrent():null;
                activityModel=null!=currentModel&&currentModel instanceof Model?((Model)currentModel):null;
            }
            if (null!=activityModel){
                //Call onCreate
                if (activityModel instanceof OnActivityCreate) {
                    ((OnActivityCreate)activityModel).onActivityCreated(activity,savedInstanceState);
                }
                //Collection register broadcasts
                List<IntentFilter> intentFilters=activityModel instanceof OnModelBroadcastResolve ?((OnModelBroadcastResolve)activityModel).onBroadcastResolve(null):null;
                intentFilters=activity instanceof OnModelBroadcastResolve?((OnModelBroadcastResolve)activity).onBroadcastResolve(intentFilters):intentFilters;
                registerActivityBroadcast(activity,activityModel,intentFilters,"While activity onCreate");
                //Collection bind service
                List<Intent> intents=activityModel instanceof OnModelServiceResolve ?((OnModelServiceResolve)activityModel).onServiceResolved(null):null;
                intents=activity instanceof OnModelServiceResolve?((OnModelServiceResolve)activity).onServiceResolved(intents):intents;
                bindActivityService(activity,activityModel,intents,"While activity onCreate");
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityStart) {
            ((OnActivityStart)model).onActivityStarted(activity);
        }
    }

    @Override
    public void onActivityResumed( Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityResume) {
            ((OnActivityResume)model).onActivityResume(activity);
        }
    }

    @Override
    public void onActivityPaused( Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityPause) {
            ((OnActivityPause)model).onActivityPaused(activity);
        }
    }

    @Override
    public void onActivityStopped( Activity activity) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivityStop) {
            ((OnActivityStop)model).onActivityStopped(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState( Activity activity, Bundle outState) {
        Model model=findActivityModel(activity);
        if (null!=model&&model instanceof OnActivitySaveInstanceState) {
            ((OnActivitySaveInstanceState)model).onActivitySaveInstanceState(activity,outState);
        }
    }

    @Override
    public void onActivityDestroyed( Activity activity) {
        if(null!=activity){
            removeActivityRegister(activity, "While activity onDestroy");
            Model model=findActivityModel(activity);
            if (null!=model&&model instanceof OnActivityDestroyed) {
                ((OnActivityDestroyed)model).onActivityDestroyed(activity);
            }
        }
    }

    private boolean bindActivityService(Activity activity,Model model,List<Intent> intents,String debug){
        if (null!=intents&&intents.size()>0){//Bind each service
            for (Intent intent:intents) {
                final ServiceConnection connection=null!=intent?new ServiceConnection(){
                    @Override
                    public void onBindingDied(ComponentName name) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(null, name);
                        }
                        if (null!=model&&model instanceof OnServiceBindChange){
                            ((OnServiceBindChange)model).onServiceBindChanged(null, name);
                        }
                    }

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(service, name);
                        }
                        if (null!=model&&model instanceof OnServiceBindChange){
                            ((OnServiceBindChange)model).onServiceBindChanged(service, name);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(null, name);
                        }
                        if (null!=model&&model instanceof OnServiceBindChange){
                            ((OnServiceBindChange)model).onServiceBindChanged(null, name);
                        }
                    }}:null;
                if (activity.bindService(intent, connection, Context.BIND_AUTO_CREATE)){
                    if (!addActivityRegister(activity,connection, debug)){
                        activity.unbindService(connection);
                    }else{
                        Debug.D("Bind service "+(null!=debug?debug:".")+" "+intent);
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean registerActivityBroadcast(final Activity activity,final Model model,List<IntentFilter> intents,String debug){
        if (null!=intents&&intents.size()>0){//Bind each broadcast
            BroadcastReceiver receiver=null;
            final Handler handler=new Handler(Looper.getMainLooper());
            for (IntentFilter intent:intents) {
                if (null!=intent) {
                    activity.registerReceiver(null==receiver?receiver=new HandlerBroadcastReceiver(handler){
                        @Override
                        public void onReceive(Intent intent,Context context) {
                            if (null!=activity&&activity instanceof OnBroadcastReceive){
                                ((OnBroadcastReceive)activity).onBroadcastReceived(context, intent);
                            }
                            if (null!=model&&model instanceof OnBroadcastReceive){
                                ((OnBroadcastReceive)model).onBroadcastReceived(context, intent);
                            }
                        }
                    }:receiver, intent);
                }
            }
            if (null!=receiver){
                if (!addActivityRegister(activity,receiver,"While activity onCreate")){
                    activity.unregisterReceiver(receiver);//Fail?Rollback resister
                    return false;
                }else{
                    Debug.D("Register broadcast service "+(null!=debug?debug:".")+" "+intents);
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean removeActivityRegister(Activity activity,String debug){
        Map<Object,Activity> activityRegister=null!=activity?mActivityRegister:null;
        if (null!=activityRegister){
            Set<Object> set=activityRegister.keySet();
            if(null!=set&&set.size()>0){
                for (Object child:set) {
                    Activity value=null!=child?activityRegister.get(child):null;
                    if (null!=value&&value == activity){
                        if (child instanceof BroadcastReceiver){
                            Debug.D("Unregister broadcast receiver "+(null!=debug?debug:".")+" "+child);
                            activity.unregisterReceiver((BroadcastReceiver) child);
                        }
                        if (child instanceof ServiceConnection){
                            Debug.D("Unbind service "+(null!=debug?debug:".")+" "+child);
                            activity.unbindService((ServiceConnection) child);
                        }
                    }
                }
            }
            if (activityRegister.size()<=0){
                mActivityRegister=null;
            }
            return true;
        }
        return false;
    }

    private boolean addActivityRegister(Activity activity, Object register,String debug){
        if(null!=activity&&null!=register){
            Map<Object,Activity> activityRegister=mActivityRegister;
            activityRegister=null==activityRegister?(mActivityRegister=new HashMap<>()):activityRegister;
            if (!activityRegister.containsKey(register)) {
                return null==activityRegister.put(register,activity);
            }
            return false;
        }
        return false;
    }

    public synchronized boolean bindActivityLife(boolean enable,Context context){
        return bindLife(context,enable,(app,en)->{
            app.unregisterActivityLifecycleCallbacks(ModelLifeBinderImpl.this);
            if (enable){ app.registerActivityLifecycleCallbacks(ModelLifeBinderImpl.this); }
        });
    }

    private boolean bindLife(Context context, boolean enable, OnLifeBind onLifeBind){
        if (null!=onLifeBind){
            try {
                context=null!=context?context instanceof Application?context:context.getApplicationContext():null;
            }catch (Exception e){
                Debug.D("Can't bind model life! You must make register after call activity's super.attachBaseContext(newBase)");
            }
            if (null!=context&&context instanceof Application){
                onLifeBind.onLifeBind((Application)context,enable);
                return true;
            }
        }
        return false;
    }
}
