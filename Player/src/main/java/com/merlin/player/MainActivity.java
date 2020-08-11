package com.merlin.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.merlin.mvvm.ActivityLifeBinder;
import com.merlin.mvvm.OnModelLayoutResolve;
import com.merlin.mvvm.OnModelResolve;

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
