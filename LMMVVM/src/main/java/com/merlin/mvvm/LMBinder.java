package com.merlin.mvvm;

import android.content.Context;

/**
 * @deprecated
 */
public class LMBinder {
      private static ActivityLifeBinder mActivityLifeBinder;

      public static boolean isActivityLifeBinderEnable(){
          return null!=mActivityLifeBinder;
      }

      public static boolean enable(Context context,boolean enable,String debug){

          return false;
      }

//    private static ActivityAutoBinder mActivityBinder;
//
//    public static boolean isAutoBindActivityModelEnable(){
//        return null!=mActivityBinder;
//    }
//
//    public static boolean autoBindActivityModel(Context context){
//        if (null!=mActivityBinder){
//            return false;
//        }
//        context=null!=context?context.getApplicationContext():null;
//        if (null!=context&&context instanceof Application){
//            ((Application)context).registerActivityLifecycleCallbacks(mActivityBinder=new ActivityAutoBinder());
//            return true;
//        }
//        return false;
//    }

}
