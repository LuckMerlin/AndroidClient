package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;

import java.util.List;

public class MediaListAdapter  extends BaseAdapter{

    @Override
    protected int onResolveNormalTypeLayoutId() {
        return R.layout.item_playing_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Object data, @NonNull List payloads) {

    }
}
