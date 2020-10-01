package com.luckmerlin.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.SnapAdapter;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.mvvm.Model;


public class TestModel extends Model {

    public final ObservableField<String> ddd=new ObservableField<>();
    private SnapAdapter mSnapAdapter=new SnapAdapter(1,2,2){

    };

    private TestModel(Context context){

    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        ddd.set("sdfasdfasdfas");
    }

    public SnapAdapter getSnapAdapter() {
        return mSnapAdapter;
    }
}
