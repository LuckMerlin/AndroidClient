package com.merlin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.merlin.bean.Meta;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.model.FileBrowserModel;
import com.merlin.player.Player;
import com.merlin.player1.MediaPlayer;
import com.merlin.protocol.Tag;

import java.io.Serializable;


public final class FileBrowserActivity extends  SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {
    private Player mPlayer=new Player();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FileBrowserModel.MM=this;
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        Toast.makeText(this,""+mPlayer.play(null,0),Toast.LENGTH_LONG).show();
        Serializable serializable=null!=intent?intent.getSerializableExtra(TAG_META):null;
        Meta meta=null!=serializable&&serializable instanceof Meta?(Meta)serializable:null;
        if (null==meta||null==meta.getAccount() || !meta.isDeviceType(TAG_NAS_DEVICE)){
            toast("不能浏览非指定文件系统的终端");
            finish();
            return ;
        }
        getViewModel().setClientMeta(meta);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().refreshCurrentPath();
    }

    @Override
    public void onBackPressed() {
        if (!getViewModel().browserParent()){
            super.onBackPressed();
        }
    }

}
