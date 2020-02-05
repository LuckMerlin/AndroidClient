package com.merlin.view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.MultiPageAdapter;

public interface MultiPageAdapterRefreshBridge extends SwipeRefreshLayout.OnRefreshListener,MultiPageAdapter.OnPageLoadUpdate {
}
