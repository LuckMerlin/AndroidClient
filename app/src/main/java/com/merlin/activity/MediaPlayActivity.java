package com.merlin.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.media.MediaPlayService;
import com.merlin.media.MediaPlayer;
import com.merlin.model.BaseModel;
import com.merlin.model.MediaPlayModel;

public class MediaPlayActivity extends ModelActivity<MediaPlayModel> implements ServiceConnection{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
         MediaPlayModel model=getModel(MediaPlayModel.class);
        if (null!=model&&model instanceof MediaPlayModel){
            return ((MediaPlayModel)model).setMediaPlayer(player);
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
