package com.luckmerlin.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;
import android.view.View;
import android.widget.PopupWindow;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.SnapAdapter;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.core.proguard.PublishProtectedMethod;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.touch.OnSingleTapClick;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.mvvm.service.OnModelServiceResolve;
import com.luckmerlin.mvvm.service.OnServiceBindChange;

import java.util.ArrayList;
import java.util.List;


public class TestModel extends Model implements OnModelResolve, OnSingleTapClick,OnViewClick, OnServiceBindChange,OnModelServiceResolve, PublishMethods, PublishProtectedMethod {

    public final ObservableField<String> ddd=new ObservableField<>();
    private final TextListAdapter mAdapter=new TextListAdapter();
    private SnapAdapter mSnapAdapter=new SnapAdapter(1,2,2){

    };

    public TestModel(){
        Debug.D("AAAAAAAAAAAA" +this);
    }

    @Override
    public List<Intent> onServiceResolved(List<Intent> list) {
        (null!=list?list:(list=new ArrayList<>())).add(new Intent(getContext(),TestSevice.class));
        return list;
    }

    @Override
    public void onServiceBindChanged(IBinder binder, ComponentName componentName) {
        Debug.D("DDDonServiceBindChangedDDDDdd  "+binder);
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

    private boolean fullscreen(boolean fullscreen, String debug){
        Context context=getContext();
        Activity activity=null!=context&&context instanceof Activity?((Activity)context):null;
        if (null!=activity){
            int current=activity.getRequestedOrientation();
            int target=fullscreen? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (target!=current){
                activity.setRequestedOrientation(target);
                activity.getWindow().getDecorView().setSystemUiVisibility(fullscreen?View.SYSTEM_UI_FLAG_FULLSCREEN: View.SYSTEM_UI_FLAG_VISIBLE);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onViewSingleTap(View view, int resId, Object tag) {
        Debug.D("单击QQQQQQq  "+resId+" "+view+" "+tag);
        fullscreen(true,"");
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

    public TextListAdapter getAdapter() {
        return mAdapter;
    }
}
