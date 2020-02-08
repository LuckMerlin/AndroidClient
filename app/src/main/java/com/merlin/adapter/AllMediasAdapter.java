package com.merlin.adapter;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.FileMeta;
import com.merlin.bean.Media;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaAllBinding;
import com.merlin.client.databinding.ItemMediaBinding;

import java.util.List;

public abstract class AllMediasAdapter extends MultiPageAdapter<String, FileMeta, PageData<FileMeta>>  {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return  R.layout.item_media_all;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, FileMeta data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemMediaAllBinding){
            ((ItemMediaAllBinding)binding).setMediaFile(data);
            ((ItemMediaAllBinding)binding).setPosition(position);
            ((ItemMediaAllBinding)binding).setPlaying(false);
        }
    }

    public final boolean notifyFavoriteChange(String path, boolean favorite){
        if (null!=path&&path.length()>0){
            List<FileMeta> list=getData();
            int length=null!=list?list.size():0;
            for (int i = 0; i < length; i++) {
                FileMeta media=list.get(i);
                String curr=null!=media?media.getPath():null;
                if (null!=curr&&curr.equals(path)){
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
