package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface LayoutManagerResolver extends PublishMethods {
    RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv);
}
