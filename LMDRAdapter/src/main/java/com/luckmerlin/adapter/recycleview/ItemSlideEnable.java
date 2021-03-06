package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface ItemSlideEnable extends PublishMethods {
    Integer onResolveSlide(RecyclerView.ViewHolder viewHolder, RecyclerView.LayoutManager manager);
}
