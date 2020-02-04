package com.merlin.model;

import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.client.R;
import com.merlin.debug.Debug;

public class ActivityMediaPlayModel extends Model{
    private final MediaPlayDisplayAdapter mDisplayAdapter=new MediaPlayDisplayAdapter();

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
//        RecyclerView recyclerView=findViewById(R.id.test, RecyclerView.class);
//        recyclerView.setAdapter(getDisplayAdapter());
    }

    public MediaPlayDisplayAdapter getDisplayAdapter() {
        return mDisplayAdapter;
    }
}
