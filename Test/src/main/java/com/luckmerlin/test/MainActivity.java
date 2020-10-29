package com.luckmerlin.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.mvvm.ModelLifeBinder;
import com.luckmerlin.test.databinding.ActivityMainBinding;

public class MainActivity extends Activity  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Debug.D("RRRRRRRRRRRRRRRRRRRRr  ");
        super.onCreate(savedInstanceState);
        Dialog dialog=new Dialog(this);
        dialog.setContentView(new TestModel());
        dialog.show();
    }

    @Override

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        ModelLifeBinder.bindActivityLife(true,this);
    }


}
