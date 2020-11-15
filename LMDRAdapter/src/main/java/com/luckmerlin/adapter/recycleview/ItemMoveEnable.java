package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface ItemMoveEnable extends PublishMethods {
    Integer onResolveMove(RecyclerView.ViewHolder viewHolder, RecyclerView.LayoutManager manager);
}
