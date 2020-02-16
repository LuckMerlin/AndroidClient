package com.merlin.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.MediaPlayService;
import com.merlin.media.MediaPlayer;
import com.merlin.model.BaseModel;
import com.merlin.model.MediaPlayModel;
import com.merlin.model.Model;
import com.merlin.model.OnPlayerBindChange;

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
        DataBindingUtil.setContentView(this,R.layout.activity_media_play);
        MediaPlayService.bind(this);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (null!=service&&service instanceof MediaPlayer){
            setMediaPlayer((MediaPlayer)service);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        setMediaPlayer(null);
    }

    private boolean setMediaPlayer(MediaPlayer player){
         Model model=getModel();
        if (null!=model&&model instanceof OnPlayerBindChange){
            ((OnPlayerBindChange)model).onPlayerBindChanged(player);
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setMediaPlayer(null);
        MediaPlayService.unbind(this);
    }
}
