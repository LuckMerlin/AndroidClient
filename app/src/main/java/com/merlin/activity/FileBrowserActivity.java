package com.merlin.activity;

import android.Manifest;
import android.app.Activity;
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
import com.merlin.conveyor.ConveyorBinder;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.model.Model;
import com.merlin.protocol.Tag;
import com.merlin.transport.TransportService;


public final class FileBrowserActivity extends  ModelActivity<FileBrowserModel> implements Tag {
    private static ServiceConnection mConnection;
    private ConveyorBinder mBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_file_browser);
        checkPermission(this);
        if (null==mConnection) {
            Intent intent = new Intent(this, TransportService.class);
            startService(intent);
            bindService(intent, mConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if (null!=service&&service instanceof ConveyorBinder){
                        ConveyorBinder downloader=(ConveyorBinder)service;
                        mBinder=downloader;
                        setBinder(downloader,"After bind succeed");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mBinder=null;
                    setBinder(null,"After bind disconnected");
                    Debug.D(getClass(),"####BrowserActivity####  onServiceDisconnected");
                }
            }, Context.BIND_AUTO_CREATE);
        }
    }

    public static  void checkPermission(Activity context) {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(context, "授权成功！", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(context, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(context, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onModelBind(Model model) {
        super.onModelBind(model);
        setBinder(mBinder,"After model bind.");
    }

    private boolean setBinder(ConveyorBinder binder, String debug){
        Model model=getModel();
        return null!=model&&model instanceof Model.OnBindChange &&((Model.OnBindChange)model).onBindChanged(binder,debug);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"####BrowserActivity####  onDestroy");
        ServiceConnection connection=mConnection;
        setBinder(null,"After activity destroy.");
        if (null!=connection){
            mConnection=null;
            unbindService(connection);
        }
        mBinder=null;
    }

}
