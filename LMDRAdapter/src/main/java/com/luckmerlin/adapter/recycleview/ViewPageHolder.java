package com.luckmerlin.adapter.recycleview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.ViewCreator;

public class ViewPageHolder extends ViewHolder implements PublishMethods {

    public ViewPageHolder(ViewGroup viewGroup) {
        super(new FrameLayout(viewGroup.getContext()));
        View view=itemView;
        if (null!=view){
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        }
    }

    public final View getRoot(){
        View root=itemView;
        ViewGroup vg=null!=root&&root instanceof ViewGroup?((ViewGroup)root):null;
        int count=null!=vg?vg.getChildCount():0;
        return count==1?vg.getChildAt(0):null;
    }

    public final boolean inflateLayout(Object pageLayout) {
        View root=itemView;
        ViewGroup vg=null!=root&&root instanceof ViewGroup?((ViewGroup)root):null;
        if (null!=vg){
            vg.removeAllViews();
            if (null!=pageLayout){
                View layoutView=new ViewCreator().create(vg.getContext(),pageLayout);
                if (null!=layoutView){
                    ViewParent parent = layoutView.getParent();
                    if (null != parent && parent instanceof ViewGroup) {
                        ((ViewGroup) parent).removeView(layoutView);
                    }
                    if (null == layoutView.getParent()) {
                        vg.addView(layoutView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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