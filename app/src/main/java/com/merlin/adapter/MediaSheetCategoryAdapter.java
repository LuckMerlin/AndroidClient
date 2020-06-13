package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaSheetBinding;
import com.merlin.bean.Sheet;

import java.util.List;

public abstract class MediaSheetCategoryAdapter extends PageAdapter<String,Sheet>  {

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_media_sheet:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Sheet data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data&&binding instanceof ItemMediaSheetBinding){
            ((ItemMediaSheetBinding)binding).setSheet(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            return new GridLayoutManager(rv.getContext(), 3,RecyclerView.VERTICAL,false);
        }
        return null;
    }
}
