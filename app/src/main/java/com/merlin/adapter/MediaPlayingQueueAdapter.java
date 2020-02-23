package com.merlin.adapter;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.SectionData;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaAllBinding;
import com.merlin.client.databinding.ItemMediaPlayingBinding;
import com.merlin.player.Playable;

import java.util.List;

public class MediaPlayingQueueAdapter extends Adapter<Playable>  {

    public MediaPlayingQueueAdapter(List<Playable> list){
        setData(list);
    }

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return  R.layout.item_media_playing;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Playable data, @NonNull List<Object> payloads) {
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
