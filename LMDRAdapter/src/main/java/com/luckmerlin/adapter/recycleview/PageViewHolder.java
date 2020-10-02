package com.luckmerlin.adapter.recycleview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.ViewCreator;

public class PageViewHolder extends ViewHolder implements PublishMethods {

    public PageViewHolder(ViewGroup viewGroup) {
        super(new FrameLayout(viewGroup.getContext()));
        View view=itemView;
        if (null!=view){
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        }
    }

    private boolean tryBindBindingView(View view,String debug){
        ViewDataBinding binding=null!=view?DataBindingUtil.getBinding(view):null;
        return null!= binding&&null!=new ModelBinder().bindModelForObject(view.getContext(),binding,debug);
    }

    public boolean inflateLayout(Object pageLayout) {
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
                        tryBindBindingView(layoutView,null);
                        vg.addView((View) pageLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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