package com.merlin.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemTouchResolver {
    interface onItemDragResolver{
        Boolean onResolveItemDrag(RecyclerView.ViewHolder viewHolder, int dragFlags);
    }
    Object onResolveItemTouch(RecyclerView recyclerView);
}
