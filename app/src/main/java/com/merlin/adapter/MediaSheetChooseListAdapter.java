package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.api.SectionData;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaSheetChooseListBinding;

import java.util.List;

public abstract class MediaSheetChooseListAdapter extends MultiSectionAdapter<String,Sheet, SectionData<Sheet>> {


    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_media_sheet_choose_list;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Sheet data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemMediaSheetChooseListBinding){
            ((ItemMediaSheetChooseListBinding)binding).setSheet(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        LinearLayoutManager manager= new LinearLayoutManager(rv.getContext(),LinearLayoutManager.VERTICAL,false);
        return manager;
    }
}
