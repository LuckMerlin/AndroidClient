package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public interface TTOnMoreLoadable extends PublishMethods {
    boolean onLoadMore(RecyclerView recyclerView, int state, String debug);
}
