package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnItemSlideRemove extends PublishMethods {

    void onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover);
}
