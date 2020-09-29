package com.luckmerlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.luckmerlin.core.debug.Debug;
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
import java.util.WeakHashMap;

final class LifeBinderImpl implements Application.ActivityLifecycleCallbacks,ComponentCallbacks{
    private static LifeBinderImpl mLifeNotify;
    private Map<Object,Model> mModelMaps;
    private Map<Object,Activity> mActivityRegister;

    private interface OnIterate{
        boolean onIterated(Object value);
    }

    private interface OnLifeBind{
        void onLifeBind(Application application, boolean enable, LifeBinderImpl notify);
    }

    private LifeBinderImpl(){

    }

    private static boolean bindLife(Context context,boolean enable,OnLifeBind onLifeBind){
        if (null!=onLifeBind){
            try {
                context=null!=context?context.getApplicationContext():null;
            }catch (Exception e){
                Debug.D("Can't bind model life! You must make register after call activity's super.attachBaseContext(newBase)");
            }
            if (null!=context&&context instanceof Application){
                LifeBinderImpl lifeNotify=mLifeNotify;
                lifeNotify=enable&&null==lifeNotify?(mLifeNotify=new LifeBinderImpl()):lifeNotify;
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
            app.unregisterActivityLifecycleCallbacks(life);
            if (enable){ app.registerActivityLifecycleCallbacks(life); }
        });
    }

    public static synchronized boolean bindComponentLife(boolean enable,Context context){
        return bindLife(context,enable,(app,en,life)->{
            app.unregisterComponentCallbacks(life);
            if (enable){ app.registerComponentCallbacks(life); }
        });
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

    private boolean bindActivityService(Activity activity,Model model,List<Intent> intents,String debug){
        if (null!=intents&&intents.size()>0){//Bind each service
            for (Intent intent:intents) {
                final ServiceConnection connection=null!=intent?new ServiceConnection(){
                    @Override
                    public void onBindingDied(ComponentName name) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(null, name);
                        }
                        if (null!=model&&model instanceof OnBroadcastReceive){
                            ((OnServiceBindChange)model).onServiceBindChanged(null, name);
                        }
                    }

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(service, name);
                        }
                        if (null!=model&&model instanceof OnBroadcastReceive){
                            ((OnServiceBindChange)model).onServiceBindChanged(service, name);
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        if (null!=activity&&activity instanceof OnServiceBindChange){
                            ((OnServiceBindChange)activity).onServiceBindChanged(null, name);
                        }
                        if (null!=model&&model instanceof OnBroadcastReceive){
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

    @Override
    public void onActivityCreated(final Activity activity,  Bundle savedInstanceState) {
        if (null!=activity){
            ModelBinder binder=new ModelBinder();
            ModelBinder.Created created=binder.createModel(activity instanceof OnModelResolve?binder.getIfNotNull(
                    binder.createModel(activity,((OnModelResolve)activity).onResolveModel()),activity):activity);
            Model model=null;
            if (null!=created){
                model=created.mModel;
                View root=created.mRoot;
                if (null!=root){
                    if (null==model||binder.attachModel(root,model,"While activity onCreate.")){//Normal view
                        activity.setContentView(root,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        if (null!=model){
                            addActivityLife(activity,model,"While activity onCreate.");
                        }
                    }
                }
            }
            //Call onCreate
            if (null!=model&&model instanceof OnActivityCreate) {
                ((OnActivityCreate)model).onActivityCreated(activity,savedInstanceState);
            }
            //Collection register broadcasts
            List<IntentFilter> intentFilters=null!=model&&model instanceof OnModelBroadcastResolve?((OnModelBroadcastResolve)model).onBroadcastResolve(null):null;
            intentFilters=activity instanceof OnModelBroadcastResolve?((OnModelBroadcastResolve)activity).onBroadcastResolve(intentFilters):intentFilters;
            registerActivityBroadcast(activity,model,intentFilters,"While activity onCreate");
            //Collection bind service
            List<Intent> intents=null!=model&&model instanceof OnModelServiceResolve ?((OnModelServiceResolve)model).onServiceResolved(null):null;
            intents=activity instanceof OnModelServiceResolve?((OnModelServiceResolve)activity).onServiceResolved(intents):intents;
            bindActivityService(activity,model,intents,"While activity onCreate");
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
            Model model=removeModel(activity,"While activity onDestroy.");
            if (null!=model&&model instanceof OnActivityDestroyed) {
                ((OnActivityDestroyed)model).onActivityDestroyed(activity);
            }
            removeActivityRegister(activity, "While activity onDestroy");
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

    private Model removeModel(Context context,String debug){
        Map<Object,Model> modelMap=null!=context?mModelMaps:null;
        Model removed=null;
        if (null!=modelMap){
            removed=modelMap.remove(context);
        }
        new ModelBinder().unbindViewModel(debug,removed,context);
        if (null!=modelMap&&modelMap.size()<=0){
            mModelMaps=null;
        }
        return removed;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        iterateEach((object)-> null!=object&&object instanceof OnConfigurationChange&&((OnConfigurationChange)object).onConfigurationChanged(newConfig));
    }

    @Override
    public void onLowMemory() {
        iterateEach((object)-> null!=object&&object instanceof OnLowMemory&&((OnLowMemory)object).onLowMemory());
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
