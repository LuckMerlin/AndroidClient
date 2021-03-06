package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.IPath;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemWebsiteCategoryBinding;
import com.merlin.website.TravelCategory;
import com.merlin.website.WebsiteModel;

import java.util.List;

public abstract class WebsiteCategoriesAdapter extends PageAdapter<String, TravelCategory>{

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType == TYPE_DATA ? R.layout.item_website_category : null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, TravelCategory data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemWebsiteCategoryBinding){
            IPath path=null!=data?data.getUrl():null;
            if (null!=path){
                path.setHost(WebsiteModel.SERVER_IP);
                path.setPort(WebsiteModel.SERVER_PORT);
            }
            ((ItemWebsiteCategoryBinding)binding).setCategory(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new GridLayoutManager(rv.getContext(),2,RecyclerView.VERTICAL,false);
    }
}
