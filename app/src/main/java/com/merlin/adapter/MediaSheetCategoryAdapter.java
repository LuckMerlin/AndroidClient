package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemMediaSheetBinding;
import com.merlin.bean.Sheet;
import java.util.List;

public class MediaSheetCategoryAdapter extends MultiPageAdapter<Sheet> {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_media_sheet;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Sheet data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data&&binding instanceof ItemMediaSheetBinding){
            ((ItemMediaSheetBinding)binding).setSheet(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            GridLayoutManager glm=new GridLayoutManager(rv.getContext(), 3,RecyclerView.VERTICAL,false);
            glm.setSmoothScrollbarEnabled(true);
            return glm;
        }
        return null;
    }
}
