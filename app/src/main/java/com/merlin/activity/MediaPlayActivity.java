package com.merlin.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.media.MediaPlayService;
import com.merlin.model.ActivityMediaPlayModel;
import com.merlin.model.Model;
import com.merlin.model.OnPlayerBindChange;
import com.merlin.player1.MPlayer;

public class MediaPlayActivity extends ModelActivity implements ServiceConnection{

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2333);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2333);
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        DataBindingUtil.setContentView(this,R.layout.activity_media_play);
        MediaPlayService.bind(this);
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayService.unbind(this);
    }
}
