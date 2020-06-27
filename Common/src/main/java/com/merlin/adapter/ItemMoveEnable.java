package com.merlin.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemMoveEnable {
    Integer onResolveMove(RecyclerView.ViewHolder viewHolder,RecyclerView.LayoutManager manager);
}
