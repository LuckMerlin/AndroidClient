package com.merlin.website;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.GridSpacingItemDecoration;
import com.merlin.adapter.PageAdapter;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemPhotoGridBinding;

import java.util.List;

public abstract class WebsiteCategoryPhotoAdapter extends PageAdapter<String, Path> {

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA? R.layout.item_photo_grid:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding,
                                    int position, Path data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemPhotoGridBinding){
            if (null!=data&&(null==data.getHost()||data.getPort()<=0)){
                data.setHost(WebsiteModel.SERVER_IP);
                data.setPort(WebsiteModel.SERVER_PORT);
            }
            ((ItemPhotoGridBinding)binding).setPhoto(data);
            ((ItemPhotoGridBinding)binding).setPhotoUrl(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        int spanCount=3;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount<=0?1:spanCount);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rv.addItemDecoration(new GridSpacingItemDecoration(spanCount, 10, true));
        gridLayoutManager.setSmoothScrollbarEnabled(true);
        return gridLayoutManager;
    }

}
