package com.merlin.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.merlin.bean.ClientMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.model.FileBrowserModel;
import com.merlin.protocol.Tag;


public final class FileBrowserActivity extends  ModelActivity<FileBrowserModel> implements Tag {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_file_browser);
        checkPermission(this);
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


}
