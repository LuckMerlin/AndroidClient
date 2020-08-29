package com.luckmerlin.databinding.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface LayoutManagerResolver {
    RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv);
}
