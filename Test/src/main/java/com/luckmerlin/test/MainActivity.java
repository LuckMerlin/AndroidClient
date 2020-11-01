package com.luckmerlin.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.OnModelResolve;
import com.luckmerlin.databinding.dialog.AlertDialog;
import com.luckmerlin.databinding.dialog.Dialog;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.mvvm.ModelLifeBinder;
import com.luckmerlin.test.databinding.ActivityMainBinding;

public class MainActivity extends Activity implements OnViewClick {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog dialog=new AlertDialog(this);
        dialog.setContentView(new NotifyDialogModel(){

                              }
        );
        dialog.show(new OnViewClick() {
            @Override
            public boolean onViewClick(View view, int resId, int count, Object tag) {
                Debug.D("DDDonViewClickDDDDDDDDDD  "+view+" "+resId);
                return false;
            }
        });
    }

    @Override

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        ModelLifeBinder.bindActivityLife(true,this);
    }

    @Override
    public boolean onViewClick(View view, int resId, int count, Object tag) {
        Debug.D("DDDDDDDDDDDDD  "+view+" "+resId);
        return false;
    }
}
