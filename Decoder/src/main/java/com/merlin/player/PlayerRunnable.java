package com.merlin.player;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;

abstract class PlayerRunnable  implements Runnable{
     private Handler mHandler=null;
     private final String mName;

     protected PlayerRunnable(String name){
         mName=name;
     }

     protected abstract void onRun();

     @Override
     public final void run() {
         String name=mName;
         Debug.D(getClass(), (null!=name?name:"")+" start");
         Looper.prepare();
         Handler handler=mHandler=new Handler(Looper.myLooper());
         onRun();
         Looper.loop();
         mHandler=null;
         Debug.D(getClass(), (null!=name?name:"")+" stop");
     }

     public final boolean post(Runnable runnable,int delay){
         Handler handler=mHandler;
         return null!=handler&&handler.postDelayed(runnable,delay<=0?0:delay);
     }

     public final boolean quit()

     {
         Handler handler=mHandler;
         Looper looper=null!=handler?handler.getLooper():null;
         if (null!=looper){
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                 looper.quitSafely();
             }else{
                 looper.quit();
             }
             return true;
         }
         return false;
     }
 }
