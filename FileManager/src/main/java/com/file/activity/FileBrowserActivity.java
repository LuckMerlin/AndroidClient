package com.file.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.file.model.FileBrowserModel;
import com.merlin.file.transport.FileTaskService;
import com.merlin.model.Model;
import com.merlin.model.ModelActivity;
import com.merlin.model.OnServiceBindChange;


public final class FileBrowserActivity extends ModelActivity<FileBrowserModel> {
    private static ServiceConnection mConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_file_browser);
        checkPermission(this);
        if (null==mConnection) {
            bindService(new Intent(this,FileTaskService.class),mConnection=new ServiceConnection() {
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
    protected void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"####BrowserActivity####  onDestroy");
        ServiceConnection connection=mConnection;
        if (null!=connection){
            mConnection=null;
            unbindService(connection);
            Model model=getModel();
            if (null!=model&&model instanceof OnServiceBindChange){
                ((OnServiceBindChange)model).onServiceBindChanged(null,null);
            }
        }
    }

}
