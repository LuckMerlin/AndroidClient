package com.merlin.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemPlayingMediaBinding;
import com.merlin.media.Media;

import java.util.List;

public class MediaListAdapter  extends BaseAdapter<Media, ItemPlayingMediaBinding>{
    private final RecyclerView.LayoutManager mLayoutManager;

    public MediaListAdapter(Context context){
        mLayoutManager=new LinearLayoutManager(context);
    }

    @Override
    protected int onResolveNormalTypeLayoutId() {
        return R.layout.item_playing_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemPlayingMediaBinding binding, int position, Media data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setMedia(data);
            binding.setPosition(position);
        }
    }

    public final RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }
}
