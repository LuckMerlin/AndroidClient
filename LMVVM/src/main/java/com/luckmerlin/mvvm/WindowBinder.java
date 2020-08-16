package com.luckmerlin.mvvm;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.luckmerlin.core.debug.Debug;

class WindowBinder {

     public boolean bindForModel(View view, Model model, String debug){
         if (null!=view&&null!=model){
             view.setOnTouchListener(new View.OnTouchListener() {
                 @Override
                 public boolean onTouch(View v, MotionEvent event) {
                     Debug.D("dddddddd "+event.getAction());
                     return true;
                 }
             });
//             if (null!=)
             View root=view.getRootView();
             Context context=view.getContext();
             view.getWindowToken();
             ActivityManager manager;
             WindowManager windowManager;
             Window window;
             Debug.D("dddddddd "+root);
         }
         return false;
     }
}
