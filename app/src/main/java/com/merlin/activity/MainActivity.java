package com.merlin.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.protocol.Tag;


public class MainActivity extends SocketActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {

    public void ddd(View view){
        System.exit(1);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        FileBrowserModel model=getViewModel();
        if (null==model||!model.browserParent()){
            super.onBackPressed();
        }
    }
}
