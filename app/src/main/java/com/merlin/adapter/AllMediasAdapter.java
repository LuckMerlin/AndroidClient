package com.merlin.adapter;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaAllBinding;

import java.util.List;

public abstract class AllMediasAdapter extends MultiSectionAdapter<String, NasMedia, PageData<NasMedia>>  {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return  R.layout.item_media_all;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, NasMedia data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemMediaAllBinding){
            ((ItemMediaAllBinding)binding).setMedia(data);
            ((ItemMediaAllBinding)binding).setPosition(position);
            ((ItemMediaAllBinding)binding).setPlaying(false);
        }
    }

    public final boolean notifyFavoriteChange(String md5, boolean favorite){
        if (null!=md5&&md5.length()>0){
            List<NasMedia> list=getData();
            int length=null!=list?list.size():0;
            for (int i = 0; i < length; i++) {
                NasMedia media=list.get(i);
                String curr=null!=media?media.getMd5():null;
                if (null!=curr&&curr.equals(md5)){
                    media.setFavorite(favorite);
                    notifyItemChanged(i);
                    return true;
                }
            }
        }
        return false;
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
