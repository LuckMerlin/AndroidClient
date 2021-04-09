package com.luckmerlin.test;

import android.view.View;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ui.OnSingleTapClick;
import com.luckmerlin.databinding.ui.OnViewClick;

public class DDD extends Model implements OnViewClick, OnSingleTapClick {
//    private final TestAdapter mTestAdapter=new TestAdapter();

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
    }

//    public TestAdapter getTestAdapter() {
//        return mTestAdapter;
//    }
}
