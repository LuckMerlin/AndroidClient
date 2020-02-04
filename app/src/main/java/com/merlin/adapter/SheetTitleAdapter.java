package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.SheetTitle;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemSheetTitleBinding;

import java.util.List;

public class SheetTitleAdapter extends PageAdapter<SheetTitle, ItemSheetTitleBinding> {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_sheet_title;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemSheetTitleBinding binding, int position, SheetTitle data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, binding, position, data, payloads);
        if (null!=binding){
            binding.setSheet(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            rv.addItemDecoration(new LinearItemDecoration(5));
            LinearLayoutManager llm=new LinearLayoutManager(rv.getContext(), RecyclerView.HORIZONTAL,false);
            llm.setSmoothScrollbarEnabled(true);
            return llm;
        }
        return null;
    }
}
