package com.luckmerlin.mvvm;

import android.app.Activity;
import android.content.Context;

public class ModelActivity extends Activity {
    private

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        if (this instanceof OnModelResolve){

        }
    }


    protected final Model getActivityModel(){
        return null;
    }
}
