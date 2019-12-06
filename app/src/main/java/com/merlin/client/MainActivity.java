package com.merlin.client;

import android.view.View;

import com.merlin.activity.BaseActivity;
import com.merlin.client.databinding.ActivityMainBinding;
import com.merlin.model.LoginModel;


public class MainActivity extends BaseActivity<ActivityMainBinding,LoginModel> {

    @Override
    protected int findContentViewId() {
        return R.layout.activity_main;
    }

    public void ddd(View view){
        System.exit(1);
    }

}
