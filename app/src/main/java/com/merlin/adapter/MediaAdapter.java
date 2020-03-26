package com.merlin.adapter;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.NasFile;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaBinding;

import java.util.List;
//String,Sheet, SectionData<Sheet>
public abstract class MediaAdapter extends MultiSectionAdapter<String,NasFile, PageData<NasFile>> implements OnMoreLoadable {

    public final boolean notifyFavoriteChange(String md5, boolean favorite){
        if (null!=md5&&md5.length()>0){
            List<NasFile> list=getData();
            int length=null!=list?list.size():0;
            for (int i = 0; i < length; i++) {
                NasFile meta=list.get(i);
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
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_media;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, NasFile data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemMediaBinding){
            ItemMediaBinding itemBinding=(ItemMediaBinding)binding;
            itemBinding.setMediaFile(data);
            itemBinding.setPosition(position);
            itemBinding.setPlaying(false);
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
