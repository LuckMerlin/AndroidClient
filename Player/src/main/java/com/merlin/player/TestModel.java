package com.merlin.player;

import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.core.debug.Debug;
import com.merlin.mvvm.Model;

public class TestModel extends Model {
    private ObservableField<String> mTest=new ObservableField<>();

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        Debug.D("AAAAAAAAAAAAA  "+view);
    }
}
