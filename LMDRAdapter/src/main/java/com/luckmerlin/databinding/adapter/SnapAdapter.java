package com.luckmerlin.databinding.adapter;

import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SnapAdapter<T> extends ListAdapter<T>{
    private final PagerSnapHelper mHelper=new PagerSnapHelper();

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            mHelper.attachToRecyclerView(rv);
        }
        return null;
    }

}
