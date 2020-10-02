package com.luckmerlin.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.SnapAdapter;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.mvvm.Model;
import com.luckmerlin.mvvm.activity.OnActivityBackPress;
import com.luckmerlin.mvvm.activity.OnActivityCreate;
import com.luckmerlin.mvvm.activity.OnActivityDestroyed;
import com.luckmerlin.mvvm.activity.OnActivityPause;
import com.luckmerlin.mvvm.activity.OnActivityResume;
import com.luckmerlin.mvvm.activity.OnActivityStart;


public class TestModel extends Model implements OnModelResolve,OnActivityCreate, OnActivityPause, OnActivityStart,OnActivityResume, OnActivityDestroyed {

    public final ObservableField<String> ddd=new ObservableField<>();
    private SnapAdapter mSnapAdapter=new SnapAdapter(1,2,2){

    };

    public TestModel(){
    }

    @Override
    public Object onResolveModel() {
        return R.layout.activity_main;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Debug.D("SSSSSSonActivityCreatedSSSSSSs "+activity);
    }


    @Override
    public void onActivityStarted(Activity activity) {
        Debug.D("SSSSSSonActivityStartedSSSSSSs "+activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

        Debug.D("SSSSSSononActivityPausedSSSSSSs "+activity);
    }

    @Override
    public void onActivityResume(Activity activity) {
        Debug.D("SSSSSSonActivityResumeSSSSSSs "+activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Debug.D("SSSSSSonActivityDestroyedSSSSSSs "+activity);
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        ddd.set("sdfasdfa林强sdfas");
    }

    public SnapAdapter getSnapAdapter() {
        return mSnapAdapter;
    }
}
