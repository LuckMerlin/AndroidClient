package com.luckmerlin.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.mvvm.ModelLifeBinder;
import com.luckmerlin.plugin.MPlugin;

import java.io.IOException;

public class MainActivity extends Activity implements OnModelResolve {

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(newBase);
//        ModelLifeBinder.bindActivityLife(true,this);
//        try {
//            ClassLoader classLoader=new MPlugin().createClassLoader(this,getResources().getAssets()
//                    .open("testplug-debug.apk"),null,null,null);
//            //
//            Class cls=classLoader.loadClass("com.example.testplug.CSDK");
//            Debug.D("DDDDDDDDDD  "+cls);
//        } catch (Exception e) {
//            Debug.D("DDDDDDDDDDDDD "+e);
//            e.printStackTrace();
//        }
//    }

    @Override
    public Object onResolveModel() {
        Debug.D("EEEEEEEEEEEEEEEEEEE ");
        return R.layout.test_layout;
    }
}
