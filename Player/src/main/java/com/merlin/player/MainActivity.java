package com.merlin.player;

import android.app.Activity;
import android.content.Context;

import com.luckmerlin.mvvm.ActivityLifeBinder;
import com.luckmerlin.mvvm.OnModelResolve;

public class MainActivity extends Activity implements OnModelResolve {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        ActivityLifeBinder.bindActivityLife(true,this);
    }

    @Override
    public Object onResolveModel() {
        return TestModel.class;
    }
}
