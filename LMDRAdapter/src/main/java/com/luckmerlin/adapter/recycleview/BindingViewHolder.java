package com.luckmerlin.adapter.recycleview;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.proguard.PublishMethods;

public class BindingViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder implements PublishMethods {
    private V mBinding;

    public BindingViewHolder(V binding){
        super(null!=binding?binding.getRoot():null);
        mBinding=binding;
    }

    public final V getBinding() {
        return mBinding;
    }
}
