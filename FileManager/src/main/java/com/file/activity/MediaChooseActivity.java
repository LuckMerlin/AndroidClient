package com.file.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.file.model.MediaChooseModel;
import com.merlin.file.R;
import com.merlin.model.ModelActivity;

public class MediaChooseActivity extends ModelActivity<MediaChooseModel> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_media_choose);
    }
}
