package com.merlin.view;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.MultiSectionAdapter;
import com.merlin.adapter.SectionsAdapter;


public interface SectionAdapterRefreshBridge extends SwipeRefreshLayout.OnRefreshListener, SectionsAdapter.OnPageLoadUpdate {
}
