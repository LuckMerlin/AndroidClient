package com.merlin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.merlin.bean.Meta;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.player.OnDecodeFinish;
import com.merlin.player.Player;
import com.merlin.player1.MediaPlayer;
import com.merlin.protocol.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;


public final class FileBrowserActivity extends  SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {
    private Player mPlayer=new Player();

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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FileBrowserModel.MM=this;
        super.onCreate(savedInstanceState);
        checkPermission();
        Intent intent=getIntent();
        String path="li";
//        path="/sdcard/Download/生日歌.mp3";
//        path = "/storage/151D-2906/Music/linqiang.mp3";
//        Debug.D(getClass(),"@@@@@@@@ "+new File("/sdcard/Musics/linqiang.mp3").exists());
        path="/sdcard/Musics/西单女孩 - 原点.mp3";
//        path="/sdcard/Musics/Lenka - Like A Song.mp3";
//        path="/mnt/sdcard/linqiang.mp3";
        Debug.D(getClass(),"@@ "+new File(path).exists());
        MediaPlayer dd=new MediaPlayer();
        mPlayer.setOnDecodeFinishListener(new OnDecodeFinish() {
            @Override
            public void onDecodeFinish(byte[] bytes, int channels, int sampleRate) {
                dd.play(bytes,0,bytes.length);
            }
        });
        try {
            FileInputStream is=new FileInputStream(path);
            byte[] buffer=new byte[1024*1024*5];
            int length=is.read(buffer);
            byte[] ddd=new byte[1024*1024];
            System.arraycopy(buffer,1024*614,ddd,0,1024*600);
            boolean result=mPlayer.playBytes(buffer,0,buffer.length,true);
            Debug.D(getClass(),"播放结果 "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(this,""+mPlayer.play(path,0),Toast.LENGTH_LONG).show();
        Serializable serializable=null!=intent?intent.getSerializableExtra(TAG_META):null;
        Meta meta=null!=serializable&&serializable instanceof Meta?(Meta)serializable:null;
        if (null==meta||null==meta.getAccount() || !meta.isDeviceType(TAG_NAS_DEVICE)){
            toast("不能浏览非指定文件系统的终端");
            finish();
            return ;
        }
//        getViewModel().setClientMeta(meta);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getViewModel().refreshCurrentPath();
    }

    @Override
    public void onBackPressed() {
        if (!getViewModel().browserParent()){
            super.onBackPressed();
        }
    }

}
