package com.luckmerlin.adapter.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishProtectedMethod;
import com.luckmerlin.databinding.ModelBinder;

import java.util.List;

public class ViewPagerAdapter<T> extends SnapAdapter<T> implements PublishProtectedMethod {

    public ViewPagerAdapter(T ...pages){
        super(pages);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater layoutInflater, int type, ViewGroup viewGroup) {
        return type==TYPE_DATA?new ViewPageHolder(viewGroup) :super.onCreateViewHolder(layoutInflater, type, viewGroup);
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

    protected final boolean tryBindBindingView(RecyclerView.ViewHolder holder,String debug){
        View itemRoot=null!=holder?holder instanceof ViewPageHolder ?((ViewPageHolder)holder).getRoot():holder.itemView:null;
        ViewDataBinding binding=null!=itemRoot? DataBindingUtil.getBinding(itemRoot):null;
        return null!= binding&&null!=new ModelBinder().bindModelForObject(itemRoot.getContext(),binding,debug);
    }

    @Override
    protected void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder, View view, ViewDataBinding binding) {
        super.onViewAttachedToWindow(holder, view, binding);
        tryBindBindingView(holder,"While page adapter item view attach to window.");
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, ViewDataBinding binding, int i1, T page, @NonNull List<Object> list) {
        if (null!=viewHolder&&viewHolder instanceof ViewPageHolder){
            ((ViewPageHolder)viewHolder).inflateLayout(page);
        }
    }

}
