package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.MediaSheet;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaSheetBinding;

import java.util.List;

public class MediaSheetAdapter extends BaseAdapter<MediaSheet, ItemMediaSheetBinding> {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_media_sheet;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemMediaSheetBinding binding, int position, MediaSheet data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setSheet(data);
        }
    }

}
