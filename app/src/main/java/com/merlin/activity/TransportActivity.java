package com.merlin.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.merlin.client.databinding.ActivityTransportBinding;
import com.merlin.debug.Debug;
import com.merlin.model.TransportModel;
import com.merlin.task.DownloadService;
import com.merlin.task.DownloadTask;
import com.merlin.task.Downloader;

public class TransportActivity extends  SocketActivity<ActivityTransportBinding, TransportModel>  {

    private static ServiceConnection mConnection;
    private Downloader mDownloader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null==mConnection) {
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
            bindService(intent, mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                        if (null!=service&&service instanceof Downloader){
                            Downloader downloader=(Downloader)service;
                            mDownloader=downloader;
                            getViewModel().setDownloader(downloader);
                        }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Downloader downloader=mDownloader;
                    mDownloader=null;
                    getViewModel().setDownloader(null);
                }
            }, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModel().setDownloader(null);
        ServiceConnection connection=mConnection;
        if (null!=connection){
            mConnection=null;
            unbindService(connection);
        }
        Downloader downloader=mDownloader;
        mDownloader=null;
        if (null!=downloader){
            downloader.setCallback(null);
        }
    }

}
