package com.luckmerlin.databinding.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface OnMoreLoadable {
    boolean onLoadMore(RecyclerView recyclerView, int state, String debug);
}
