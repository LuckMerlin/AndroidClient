package com.luckmerlin.test;

import android.view.View;

import com.luckmerlin.adapter.recycleview.ListAdapter;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.touch.OnSingleTapClick;
import com.luckmerlin.databinding.touch.OnViewClick;

public class TextListAdapter extends ListAdapter<String> implements OnSingleTapClick, OnViewClick {

    @Override
    public boolean onViewSingleTap(View view, int resId, Object tag) {
        Debug.D("sfdasdfasd  "+view+" "+resId);
        return false;
    }

    @Override
    public boolean onViewClick(View view, int resId, int count, Object tag) {
        Debug.D("sfdaonViewClicksdfasd  "+view+" "+resId);
        return false;
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.ss:null;
    }
}
