package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.SectionData;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemLoveBinding;

import java.util.List;

public abstract class LoveAdapter extends MultiSectionAdapter<String,Love,SectionData<Love>> implements ItemSlideRemover.OnItemSlideRemove, OnItemTouchResolver{
    private final ItemSlideRemover mItemSlideRemover=new ItemSlideRemover();

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_love;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, Love data, @NonNull List<Object> payloads) {
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
