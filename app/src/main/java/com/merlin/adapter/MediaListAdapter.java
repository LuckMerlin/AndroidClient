package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemPlayingMediaBinding;
import com.merlin.bean.Music;

import java.util.List;

public class MediaListAdapter  extends BaseAdapter<Music, ItemPlayingMediaBinding>{
    private  RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_playing_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemPlayingMediaBinding binding, int position, Music data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setMedia(data);
            binding.setPosition(position);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null==mLayoutManager){
            mLayoutManager=null!=rv?new LinearLayoutManager(rv.getContext()):null;
        }
        return mLayoutManager;
    }
}
