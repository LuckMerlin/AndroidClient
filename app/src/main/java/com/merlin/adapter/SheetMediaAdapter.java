package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemSheetMediaBinding;

import java.util.List;

public class SheetMediaAdapter extends BaseAdapter<NasMedia, ItemSheetMediaBinding> {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_sheet_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemSheetMediaBinding binding, int position, NasMedia data, @NonNull List<Object> payloads) {
        if (null!=binding){
//            binding.setMedia(data);
            binding.setPosition(position);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return null;
    }
}
