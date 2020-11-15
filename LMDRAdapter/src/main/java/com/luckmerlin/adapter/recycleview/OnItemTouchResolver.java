package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnItemTouchResolver extends PublishMethods {
    Object onResolveItemTouch(RecyclerView recyclerView);
}
