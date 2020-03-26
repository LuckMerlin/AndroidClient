package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemLoveBinding;

import java.util.List;

public abstract class LoveAdapter extends PageAdapter<String,Love> implements ItemSlideRemover.OnItemSlideRemove, OnItemTouchResolver{
    private final ItemSlideRemover mItemSlideRemover=new ItemSlideRemover();

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_love:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Love data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemLoveBinding){
            ((ItemLoveBinding)binding).setPosition(position+1);
            ((ItemLoveBinding)binding).setLove(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        LinearLayoutManager lm=new LinearLayoutManager(rv.getContext(),LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(rv.getContext(),lm.getOrientation());
        rv.addItemDecoration(itemDecoration);
        mItemSlideRemover.setOnItemSlideRemove(this);
        return lm;
    }

    @Override
    public Object onResolveItemTouch(RecyclerView recyclerView) {
        return mItemSlideRemover;
    }
}
