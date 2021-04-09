//package com.luckmerlin.test;
//
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.luckmerlin.core.debug.Debug;
//import com.luckmerlin.databinding.ui.OnSingleTapClick;
//import com.luckmerlin.databinding.ui.OnViewClick;
//
//public class TestAdapter extends ListAdapter<Integer> implements OnSingleTapClick, OnViewClick {
//
//
//    @Override
//    protected Object onResolveDataViewHolder(ViewGroup parent) {
//        return R.layout.layout;
//    }
//
//    @Override
//    public boolean onViewSingleTap(View view, int resId, Object tag) {
//        Debug.D("林强 onViewSingleTap "+resId+" "+tag);
//        return false;
//    }
//
//    @Override
//    public boolean onViewClick(View view, int resId, int count, Object tag) {
//        Debug.D("林强 onViewClick "+resId+" "+tag);
//        return true;
//    }
//}
