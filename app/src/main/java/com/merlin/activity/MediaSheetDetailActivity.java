package com.merlin.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.client.R;
import com.merlin.model.MediaSheetDetailModel;
import com.merlin.protocol.Tag;

public class MediaSheetDetailActivity  extends  ModelActivity<MediaSheetDetailModel> implements Tag {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_media_sheet_detail);
    }
}
