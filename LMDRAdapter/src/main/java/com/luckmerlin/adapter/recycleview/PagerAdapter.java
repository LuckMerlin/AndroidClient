package com.luckmerlin.adapter.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishProtectedMethod;

import java.util.List;

public class PagerAdapter<T> extends SnapAdapter<T> implements PublishProtectedMethod {

    public PagerAdapter(T ...pages){
        super(pages);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, int type, ViewGroup viewGroup) {
        return type==TYPE_DATA?new PageViewHolder(viewGroup) :super.onCreateViewHolder(layoutInflater, type, viewGroup);
    }

    protected Integer onResolveLayoutOrientation(){
        return RecyclerView.HORIZONTAL;
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView recyclerView) {
        Context context=null!=recyclerView?recyclerView.getContext():null;
        Integer orientation=onResolveLayoutOrientation();
        return null!=context?new LinearLayoutManager(context, null==orientation||(orientation!=
                RecyclerView.HORIZONTAL&&orientation!=RecyclerView.VERTICAL)?RecyclerView.HORIZONTAL:orientation,false):null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ViewDataBinding binding, int i1, T page, @NonNull List<Object> list) {
        if (null!=viewHolder&&viewHolder instanceof PageViewHolder){
            ((PageViewHolder)viewHolder).inflateLayout(page);
        }
    }

}
