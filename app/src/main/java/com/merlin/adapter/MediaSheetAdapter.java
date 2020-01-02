package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaSheetBinding;
import com.merlin.media.Sheet;

import java.util.List;

public class MediaSheetAdapter extends BaseAdapter<Sheet, ItemMediaSheetBinding> {

    @Override
    protected int onResolveNormalTypeLayoutId() {
        return R.layout.item_media_sheet;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemMediaSheetBinding binding, int position, Sheet data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setSheet(data);
        }
    }

}
