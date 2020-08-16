package com.merlin.player;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBinding;
import com.luckmerlin.mvvm.LifeBinder;
import com.luckmerlin.mvvm.OnModelResolve;
import com.luckmerlin.mvvm.Test;

public class MainActivity extends Activity implements OnModelResolve {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        LifeBinder.bindActivityLife(true,this);
//        new Thread(()->{
//            while (true){
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                new Test().test();
//                Debug.D("\n");
//            }
//        }).start();
    }

    @Override
    public Object onResolveModel() {
        return TestModel.class;
    }
}
