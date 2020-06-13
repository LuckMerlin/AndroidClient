package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaAllBinding;
import com.merlin.player1.NasMedia;

import java.util.List;

public abstract class AllMediasAdapter extends PageAdapter<String, NasMedia>  {

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_media_all:null;
    }

    public final boolean notifyByMd5(String md5){
        int index=null!=md5?indexByMd5(md5):-1;
        if (index>=0){
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    public int indexByMd5(String md5){
        List<NasMedia> media=null!=md5?getData():null;
        int size=null!=media?media.size():-1;
        NasMedia child=null;String childMd5;
        for (int i = 0; i < size; i++) {
            if (null!=(child=media.get(i))&&null!=(childMd5=child.getMd5())&&childMd5.equals(md5)){
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, NasMedia data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemMediaAllBinding){
            ((ItemMediaAllBinding)binding).setMedia(data);
            ((ItemMediaAllBinding)binding).setPosition(position);
            ((ItemMediaAllBinding)binding).setPlaying(false);
            ((ItemMediaAllBinding)binding).setFavorite(data.isFavorite());
        }
    }

    public final boolean notifyFavoriteChange(String md5, boolean favorite){
//        if (null!=md5&&md5.length()>0){
//            List<NasMedia> list=getData();
//            int length=null!=list?list.size():0;
//            for (int i = 0; i < length; i++) {
//                NasMedia media=list.get(i);
//                String curr=null!=media?media.getMd5():null;
//                if (null!=curr&&curr.equals(md5)){
//                    media.setFavorite(favorite);
//                    notifyItemChanged(i);
//                    return true;
//                }
//            }
//        }
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
