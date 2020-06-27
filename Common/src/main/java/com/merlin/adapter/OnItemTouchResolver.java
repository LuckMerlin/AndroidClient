package com.merlin.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemTouchResolver {
    Object onResolveItemTouch(RecyclerView recyclerView);
}
