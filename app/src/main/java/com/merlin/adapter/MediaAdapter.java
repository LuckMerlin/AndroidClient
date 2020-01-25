package com.merlin.adapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaBinding;
import com.merlin.media.Media;

import java.util.List;

public class MediaAdapter extends BaseAdapter<Media, ItemMediaBinding> implements BaseAdapter.OnLayoutManagerResolve {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemMediaBinding binding, int position, Media data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, binding, position, data, payloads);
        if (null!=data&&null!=binding){
            binding.setMedia(data);
            binding.setPosition(position);
            binding.setPlaying(false);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            rv.addItemDecoration(new LinearItemDecoration(3));
            LinearLayoutManager llm=new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
            llm.setSmoothScrollbarEnabled(true);
            return llm;
        }
        return null;
    }
}
