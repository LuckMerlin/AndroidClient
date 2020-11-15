package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveResolver implements ItemMoveEnable {

    @Override
    public Integer onResolveMove(RecyclerView.ViewHolder viewHolder, RecyclerView.LayoutManager manager) {
        Integer dragFlags = null;
        if (null!=manager&&manager instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        }else {
            dragFlags= ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return dragFlags;
    }
}
