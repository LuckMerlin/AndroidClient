package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.Media;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemSheetMediaBinding;

import java.util.List;

public abstract class SheetMediasAdapter extends MultiPageAdapter<String, Media, PageData<Media>> {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_sheet_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Media data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemSheetMediaBinding){
            ((ItemSheetMediaBinding)binding).setPosition(position);
            ((ItemSheetMediaBinding)binding).setMedia(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }
}
