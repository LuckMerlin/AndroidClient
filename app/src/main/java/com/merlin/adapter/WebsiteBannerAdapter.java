package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.databinding.ItemBannerBinding;
import com.merlin.view.OnTapClick;

import java.util.List;

public abstract class WebsiteBannerAdapter extends PageAdapter<String,ItemBannerBinding>{

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, ItemBannerBinding data, @NonNull List<Object> payloads) {

    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
    }
}
