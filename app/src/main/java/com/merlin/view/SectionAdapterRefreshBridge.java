package com.merlin.view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.PageAdapter;


public interface SectionAdapterRefreshBridge extends SwipeRefreshLayout.OnRefreshListener, PageAdapter.OnPageLoadUpdate {
}
