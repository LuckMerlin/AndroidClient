package com.merlin.mvvm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import com.merlin.mvvm.activity.OnActivityCreate;
import com.merlin.mvvm.activity.OnActivityDestroyed;
import com.merlin.mvvm.activity.OnActivityPause;
import com.merlin.mvvm.activity.OnActivityResume;
import com.merlin.mvvm.activity.OnActivitySaveInstanceState;
import com.merlin.mvvm.activity.OnActivityStart;
import com.merlin.mvvm.activity.OnActivityStop;

import java.util.Map;
import java.util.WeakHashMap;

final class ActivityAutoBinder extends ModelBinder implements Application.ActivityLifecycleCallbacks {
 private Map<Activity,Model> mModels;

 @Override
 public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
  if (null!=activity){
   Model model=createDeclaredModel(activity);
   Object modelLayout=null!=model&&model instanceof ModelLayoutResolver?((ModelLayoutResolver)model).onResolveModelLayout():null;
   View view=null!=modelLayout?createModelView(activity,modelLayout):null;
   if (null!=view){
       activity.setContentView(view);//Set activity model
       if (model.initialRoot(view)&&bindViewModel(model,view)){
           Map<Activity,Model> models=mModels;
           models=null!=models?models:(mModels=new WeakHashMap<>());
           models.put(activity,model);
       }
   }
   if (null!=model&&model instanceof OnActivityCreate){
        ((OnActivityCreate)model).onActivityCreated(activity,savedInstanceState);
   }
  }
 }

 @Override
 public void onActivityStarted(Activity activity) {
  Model model=getModel(activity);
  if (null!=model&&model instanceof OnActivityStart){
   ((OnActivityStart)model).onActivityStarted(activity);
  }
 }

 @Override
 public void onActivityResumed(Activity activity) {
  Model model=getModel(activity);
  if (null!=model&&model instanceof OnActivityResume){
   ((OnActivityResume)model).onActivityResume(activity);
  }
 }

 @Override
 public void onActivityPaused(Activity activity) {
  Model model=getModel(activity);
  if (null!=model&&model instanceof OnActivityPause){
   ((OnActivityPause)model).onActivityPaused(activity);
  }
 }

 @Override
 public void onActivityStopped(Activity activity) {
  Model model=getModel(activity);
  if (null!=model&&model instanceof OnActivityStop){
   ((OnActivityStop)model).onActivityStopped(activity);
  }
 }

 @Override
 public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  Model model=getModel(activity);
  if (null!=model&&model instanceof OnActivitySaveInstanceState){
   ((OnActivitySaveInstanceState)model).onActivitySaveInstanceState(activity,outState);
  }
 }

 @Override
 public void onActivityDestroyed(Activity activity) {
     Map<Activity,Model> models=null!=activity?mModels:null;
     Model model=null!=models?models.get(activity):null;
     if (null!=model){
         models.remove(model);
         if (model instanceof OnActivityDestroyed){
             ((OnActivityDestroyed)model).onActivityDestroyed(activity);
         }
     }
 }

 private Model getModel(Activity activity){
     Map<Activity,Model> models=null!=activity?mModels:null;
    return null!=models?models.get(activity):null;
 }
}
