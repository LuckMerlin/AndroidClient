package com.merlin.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
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
