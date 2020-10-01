package com.luckmerlin.adapter.recycleview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.DataBindingUtil;

public class PageViewHolder extends ViewHolder implements PublishMethods {

    public PageViewHolder(ViewGroup viewGroup) {
        super(new FrameLayout(viewGroup.getContext()));
        View view=itemView;
        if (null!=view){
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT));
        }
    }

    public boolean inflateLayout(Object pageLayout) {
        View root=itemView;
        ViewGroup vg=null!=root&&root instanceof ViewGroup?((ViewGroup)root):null;
        if (null!=vg){
            vg.removeAllViews();
            if (null!=pageLayout){
                LayoutInflater inflater=LayoutInflater.from(vg.getContext());
                if (pageLayout instanceof Integer){
                    return null!=DataBindingUtil.inflate(inflater,(Integer)pageLayout,vg,true);
                }else if (pageLayout instanceof ViewDataBinding){
                    pageLayout=((ViewDataBinding)pageLayout).getRoot();
                }
                if (pageLayout instanceof View) {
                    ViewParent parent = ((View) pageLayout).getParent();
                    if (null != parent && parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView((View) pageLayout);
                    }
                    if (null == ((View) pageLayout).getParent()) {
                        vg.addView((View) pageLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }
}