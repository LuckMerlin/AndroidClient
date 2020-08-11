package com.merlin.player;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.core.debug.Debug;
import com.merlin.mvvm.ActivityLifeBinder;
import com.merlin.mvvm.OnModelLayoutResolve;
import com.merlin.mvvm.OnModelResolve;

public class MainActivity extends Activity implements OnModelResolve, OnModelLayoutResolve {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        ActivityLifeBinder.bindActivityLife(true,this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//       View dd= View.inflate(this,R.layout.cd,null);
//        DataBindingUtil.setContentView(this,R.layout.cd);
//        setContentView(dd);
//        Debug.D("AAAAAAAAA  onCreate "+dd.getParent());
    }

    @Override
    public Object onResolveModel() {
        return TestModel.class;
    }

    @Override
    public Object onResolveModeLayout() {
        return R.layout.caobi;
    }
}
