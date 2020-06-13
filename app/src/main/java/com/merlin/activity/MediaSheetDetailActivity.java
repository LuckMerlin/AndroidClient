package com.merlin.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.media.MediaPlayService;
import com.merlin.model.MediaSheetDetailModel;
import com.merlin.model.Model;
import com.merlin.protocol.Tag;

public class MediaSheetDetailActivity  extends  ModelActivity<MediaSheetDetailModel> implements Tag, ServiceConnection {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_media_sheet_detail);
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
