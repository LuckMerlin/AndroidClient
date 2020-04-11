package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemConveyingBinding;
import com.merlin.conveyor._Convey;

import java.util.ArrayList;
import java.util.List;

public class ConveyingAdapter extends ListAdapter<_Convey> {

    public ConveyingAdapter(ArrayList<_Convey> list){
        super(list);
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA? R.layout.item_conveying:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, _Convey data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, viewType, binding, position, data, payloads);
        if (null!=binding&&binding instanceof ItemConveyingBinding){
            ItemConveyingBinding icb=(ItemConveyingBinding)binding;
            icb.setPosition(position+1);
            icb.setConvey(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
    }
}
