package com.merlin.view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.MultiPageAdapter;
import com.merlin.adapter.MultiSectionAdapter;

public interface MultiPageAdapterRefreshBridge extends SwipeRefreshLayout.OnRefreshListener, MultiSectionAdapter.OnPageLoadUpdate {
}
