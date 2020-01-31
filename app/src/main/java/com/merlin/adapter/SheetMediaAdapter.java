package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemSheetMediaBinding;
import com.merlin.bean.Media;

import java.util.List;

public class SheetMediaAdapter extends BaseAdapter<Media, ItemSheetMediaBinding> {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_sheet_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemSheetMediaBinding binding, int position, Media data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setMedia(data);
            binding.setPosition(position);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return null;
    }
}
