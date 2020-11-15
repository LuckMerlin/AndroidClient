package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public final class ItemSlideRemover implements ItemSlideEnable {

    @Override
    public Integer onResolveSlide(RecyclerView.ViewHolder viewHolder, RecyclerView.LayoutManager manager) {
        int type=viewHolder.getItemViewType();
        if(type!= ListAdapter.TYPE_EMPTY&&type!=ListAdapter.TYPE_TAIL){
            return ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        }
        return null;
    }
}