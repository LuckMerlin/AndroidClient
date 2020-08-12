package com.merlin.player;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.mvvm.Model;
import com.luckmerlin.mvvm.OnModelLayoutResolve;
import com.luckmerlin.mvvm.activity.OnActivityCreate;
import com.luckmerlin.mvvm.activity.OnActivityDestroyed;
import com.luckmerlin.mvvm.activity.OnActivityPause;
import com.luckmerlin.mvvm.activity.OnActivityResume;
import com.luckmerlin.mvvm.activity.OnActivityStart;
import com.luckmerlin.mvvm.activity.OnActivityStop;

public class TestModel extends Model implements OnModelLayoutResolve,
        OnActivityCreate, OnActivityStart, OnActivityResume, OnActivityPause, OnActivityStop,
        OnActivityDestroyed {
    private ObservableField<String> mTest=new ObservableField<>();

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        Debug.D("AAAAAAAAAAAAA  "+view);
    }

    @Override
    public Object onResolveModeLayout() {
        return R.layout.caobi;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Debug.D("AAAAAonActivityCreatedAAAAAAAA  "+activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Debug.D("onActivityDestroyed  "+activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Debug.D("onActivityPaused  "+activity);
    }

    @Override
    public void onActivityResume(Activity activity) {
        Debug.D("onActivityResume  "+activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Debug.D("onActivityStarted  "+activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Debug.D("onActivityStopped  "+activity);
    }
}
