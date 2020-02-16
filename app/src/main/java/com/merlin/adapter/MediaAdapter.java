package com.merlin.adapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaBinding;

import java.util.List;

public abstract class MediaAdapter extends PageAdapter<FileMeta,ItemMediaBinding> implements OnMoreLoadable {


    public final boolean notifyFavoriteChange(String md5, boolean favorite){
        if (null!=md5&&md5.length()>0){
            List<FileMeta> list=getData();
            int length=null!=list?list.size():0;
            for (int i = 0; i < length; i++) {
                FileMeta meta=list.get(i);
                String curr=null!=meta?meta.getMd5():null;
                if (null!=curr&&curr.equals(md5)){
                    meta.setFavorite(favorite);
                    notifyItemChanged(i);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemMediaBinding binding, int position, FileMeta data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, binding, position, data, payloads);
        if (null!=data&&null!=binding){
            binding.setMediaFile(data);
            binding.setPosition(position);
            binding.setPlaying(false);
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
