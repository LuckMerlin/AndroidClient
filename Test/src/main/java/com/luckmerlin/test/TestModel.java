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
import com.luckmerlin.databinding.touch.OnSingleTapClick;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.mvvm.Model;
import com.luckmerlin.mvvm.activity.OnActivityBackPress;
import com.luckmerlin.mvvm.activity.OnActivityCreate;
import com.luckmerlin.mvvm.activity.OnActivityDestroyed;
import com.luckmerlin.mvvm.activity.OnActivityPause;
import com.luckmerlin.mvvm.activity.OnActivityResume;
import com.luckmerlin.mvvm.activity.OnActivityStart;


public class TestModel extends Model implements OnModelResolve, OnSingleTapClick,OnViewClick {

    public final ObservableField<String> ddd=new ObservableField<>();
    private SnapAdapter mSnapAdapter=new SnapAdapter(1,2,2){

    };

    public TestModel(){
        Debug.D("AAAAAAAAAAAA" +this);
    }

    @Override
    public Object onResolveModel() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onViewClick(View view, int resId, int count, Object tag) {
        if (resId==R.string.test){
            Debug.D("文字");
        }
        Debug.D("QQQQQQQQQQQq  "+resId+" "+count+" "+tag);
        return true;
    }

    @Override
    public boolean onViewSingleTap(View view, int resId, Object tag) {
        Debug.D("单击QQQQQQq  "+resId+" "+view+" "+tag);
        return true;
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
