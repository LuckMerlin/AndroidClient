package com.file.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.file.model.TaskModel;
import com.merlin.file.R;
import com.merlin.file.transport.FileTaskService;
import com.merlin.model.Model;
import com.merlin.model.ModelActivity;
import com.merlin.model.OnServiceBindChange;

public class TaskActivity extends ModelActivity<TaskModel> {

    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Model model=getModel();
            if (null!=model&&model instanceof OnServiceBindChange){
                ((OnServiceBindChange)model).onServiceBindChanged(name,service);
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            Model model=getModel();
            if (null!=model&&model instanceof OnServiceBindChange){
                ((OnServiceBindChange)model).onServiceBindChanged(name,null);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_task);
        bindService(new Intent(this, FileTaskService.class),mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
