package com.merlin.adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface OnRecyclerScrollStateChange {
     void onRecyclerScrollStateChanged(RecyclerView recyclerView, int newState);
}
