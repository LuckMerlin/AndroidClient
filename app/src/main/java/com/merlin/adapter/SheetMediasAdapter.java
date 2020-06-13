package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.INasMedia;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemSheetMediaBinding;
import com.merlin.player.Media;
import com.merlin.player1.NasMedia;

import java.util.List;

public abstract class SheetMediasAdapter extends PageAdapter<Long, Media> {

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return TYPE_DATA==viewType?R.layout.item_sheet_media:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Media data, @NonNull List<Object> payloads) {
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
