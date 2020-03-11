package com.merlin.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.merlin.client.R;
import com.merlin.model.PhotoPreViewModel;

public class PhotoPreviewActivity extends  ModelActivity<PhotoPreViewModel>{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setModelContentView(R.layout.activity_photo_preview);
    }
}
