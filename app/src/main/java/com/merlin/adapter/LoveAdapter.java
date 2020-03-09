package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemLoveBinding;

import java.util.List;

public abstract class LoveAdapter extends MultiSectionAdapter<String,Love,SectionData<Love>> {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_love;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Love data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemLoveBinding){
            ((ItemLoveBinding)binding).setPosition(position+1);
            ((ItemLoveBinding)binding).setLove(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(),LinearLayoutManager.VERTICAL,false);
    }
}
