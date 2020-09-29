package com.luckmerlin.test;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;

import com.luckmerlin.mvvm.Model;


public class TestModel extends Model {

    public final ObservableField<String> ddd=new ObservableField<>();

    private TestModel(Context context){

    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        ddd.set("sdfasdfasdfas");
    }
}
