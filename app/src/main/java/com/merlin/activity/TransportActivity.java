package com.merlin.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.client.databinding.ActivityTransportBinding;
import com.merlin.debug.Debug;
import com.merlin.model.Model;
import com.merlin.model.TransportModel;
import com.merlin.task.DownloadService;
import com.merlin.task.Transporter;
import com.merlin.transport.TransportBinder;
import com.merlin.transport.TransportService;

public class TransportActivity extends  ModelActivity<TransportModel>  {
    private static ServiceConnection mConnection;
    private TransportBinder mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_transport);
        if (null==mConnection) {
            Intent intent = new Intent(this, TransportService.class);
            startService(intent);
            bindService(intent, mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (null!=service&&service instanceof TransportBinder){
                        TransportBinder downloader=(TransportBinder)service;
                        mBinder=downloader;
                        setBinder(downloader,"After bind succeed");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mBinder=null;
                    setBinder(null,"After bind disconnected");
                    Debug.D(getClass(),"####TransportActivity####  onServiceDisconnected");
                }
            }, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onModelBind(Model model) {
        super.onModelBind(model);
        setBinder(mBinder,"After model bind.");
    }

    private boolean setBinder(TransportBinder binder, String debug){
        Model model=getModel();
        return null!=model&&model instanceof TransportModel&&((TransportModel)model).setBinder(binder,debug);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"####TransportActivity####  onDestroy");
        ServiceConnection connection=mConnection;
        setBinder(null,"After activity destroy.");
        if (null!=connection){
            mConnection=null;
            unbindService(connection);
        }
        mBinder=null;
    }

}
