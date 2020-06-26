package com.merlin.adapter;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.PageAdapter;


public interface PageAdapterRefreshBridge extends SwipeRefreshLayout.OnRefreshListener, PageAdapter.OnPageLoadUpdate {
}
