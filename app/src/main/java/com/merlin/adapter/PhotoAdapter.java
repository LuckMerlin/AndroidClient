package com.merlin.adapter;

import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.Photo;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemPhotoBinding;

import java.util.List;

public class PhotoAdapter extends ListAdapter<Photo,ItemPhotoBinding> {
    private final int mSpanCount;

    public PhotoAdapter(int spanCount){
        mSpanCount=spanCount;
    }

//    @Override
//    protected Integer onResolveNormalTypeLayoutId() {
//        return R.layout.item_photo;
//    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemPhotoBinding binding, int position, Photo data, @NonNull List<Object> payloads) {
        if (null!=binding){
            binding.setPhoto(data);
        }
    }

//    @Override
//    protected int onIncreaseItemCount(int dataCount) {
//        return 1;
//    }


    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        int spanCount=mSpanCount;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount<=0?1:spanCount);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        return gridLayoutManager;
    }
}
