package com.merlin.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.player.MediaPlayer;
import com.merlin.protocol.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MainActivity extends SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {

    public void ddd(View view){
        System.exit(1);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        FileBrowserModel.MM=this;
        super.onCreate(savedInstanceState);
        String ddd="/storage/sdcard0/storage/emulated/0/linqiang.mp3";
        MediaPlayer player=new MediaPlayer();
        try {
            InputStream fi= getAssets().open("linqiang.mp3");
            byte[] bdd= new byte[1024*1024*5];
            int size=fi.read(bdd);
            player.play(bdd,0,size);
        } catch (Exception e) {
            Debug.E(getClass(),"ee"+e,e);
            e.printStackTrace();
        }
//        player.play();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FileBrowserModel model=getViewModel();
//        if (null!=model){
//            model.refreshCurrentPath();
//        }
    }

    @Override
    public void onBackPressed() {
        FileBrowserModel model=getViewModel();
        if (null==model||!model.browserParent()){
            super.onBackPressed();
        }
    }
}
