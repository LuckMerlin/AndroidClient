package com.merlin.player;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.core.debug.Debug;
import com.merlin.mvvm.Model;
import com.merlin.mvvm.OnModelLayoutResolve;
import com.merlin.mvvm.activity.OnActivityCreate;
import com.merlin.mvvm.activity.OnActivityDestroyed;
import com.merlin.mvvm.activity.OnActivityPause;
import com.merlin.mvvm.activity.OnActivityResume;
import com.merlin.mvvm.activity.OnActivityStart;
import com.merlin.mvvm.activity.OnActivityStop;

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
