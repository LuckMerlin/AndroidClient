package com.luckmerlin.test;

import android.app.Activity;
import android.content.Context;

import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.mvvm.ModelLifeBinder;
import com.luckmerlin.test.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements OnModelResolve {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        ModelLifeBinder.bindActivityLife(true,this);
    }

    @Override
    public Object onResolveModel() {
        return R.layout.activity_main;
    }

}
