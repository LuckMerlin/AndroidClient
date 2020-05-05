package com.merlin.website;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.merlin.activity.ModelActivity;
import com.merlin.api.Label;
import com.merlin.client.R;

public class WebsitePhotosActivity extends ModelActivity<WebsitePhotosModel> implements Label {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_website_photos);
    }
}