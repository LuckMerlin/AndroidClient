package com.merlin.player;

import android.app.Activity;
import android.content.Context;

import com.luckmerlin.mvvm.LifeBinder;
import com.luckmerlin.mvvm.OnModelResolve;

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
