package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaPlayingBinding;
import com.merlin.player.Media;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayingQueueAdapter extends ListAdapter<Media>  {

    public MediaPlayingQueueAdapter(ArrayList<Media> list){
        super(list);
    }

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_media_playing:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Media data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, viewType, binding, position, data, payloads);
        if (null!=binding&&binding instanceof ItemMediaPlayingBinding){
            ((ItemMediaPlayingBinding)binding).setMedia(data);
            ((ItemMediaPlayingBinding)binding).setPosition(position);
            ((ItemMediaPlayingBinding)binding).setPlaying(false);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            rv.addItemDecoration(new LinearItemDecoration(5));
            LinearLayoutManager llm=new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
            llm.setSmoothScrollbarEnabled(true);
            return llm;
        }
        return null;
    }
}
