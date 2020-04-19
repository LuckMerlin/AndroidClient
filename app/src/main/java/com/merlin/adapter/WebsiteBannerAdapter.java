package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.Path;
import com.merlin.bean.WebsiteImage;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemBannerBinding;
import com.merlin.client.databinding.ItemWebPhotoBinding;
import com.merlin.debug.Debug;
import com.merlin.model.WebsiteModel;
import com.merlin.view.OnTapClick;

import java.util.List;

public abstract class WebsiteBannerAdapter extends PageAdapter<String, WebsiteImage>{

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA? R.layout.item_web_photo:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, WebsiteImage data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemWebPhotoBinding){
            if (null!=data){
                data.setHost(WebsiteModel.mUrl);
            }
            ((ItemWebPhotoBinding)binding).setImage(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new GridLayoutManager(rv.getContext(),3,RecyclerView.VERTICAL,false);
    }
}
