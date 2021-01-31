package com.luckmerlin.test;

import android.view.View;
import android.view.ViewGroup;

import com.luckmerlin.adapter.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.adapter.recycleview.SectionListAdapter;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.touch.OnSingleTapClick;
import com.luckmerlin.databinding.touch.OnViewClick;

import java.util.ArrayList;
import java.util.List;

public class DDD extends Model implements OnViewClick, OnSingleTapClick {
    private final TestAdapter mTestAdapter=new TestAdapter();

    @Override
    public boolean onViewClick(View view, int resId, int count, Object tag) {
        Debug.D("QWWWWonViewClickWWWWWWWWw  "+resId+" "+count+" "+tag);
        return false;
    }

    @Override
    public boolean onViewSingleTap(View view, int resId, Object tag) {
        Debug.D("QWWWWWonViewSingleTapWWWWWWWw  "+resId+" "+tag);
        return false;
    }

    @Override
    protected void onRootAttached(View view) {
        super.onRootAttached(view);
        mTestAdapter.add(33);
        mTestAdapter.add(44);
        mTestAdapter.add(55);
        mTestAdapter.add(66);
    }

    public TestAdapter getTestAdapter() {
        return mTestAdapter;
    }
}
