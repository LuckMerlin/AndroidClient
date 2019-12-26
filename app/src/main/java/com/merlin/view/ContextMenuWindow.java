package com.merlin.view;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemContextMenuBinding;

import java.util.List;

public class ContextMenuWindow extends PopupWindow{
    private ContextAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public static class ContextMenu{
        private final int mTextId;

        public ContextMenu(int textId){
            mTextId=textId;
        }

    }

    public ContextMenuWindow(){

    }


    public boolean add(ContextMenu menu){
        ContextAdapter adapter=mAdapter;
        if (null!=adapter&&null!=menu){
            return adapter.add(menu);
        }
        return false;
    }

    @Override
    public boolean showAtLocation(View parent, int gravity, int x, int y) {
        if (null==mRecyclerView){
            mRecyclerView=new RecyclerView(parent.getContext());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
            mRecyclerView.setAdapter(mAdapter=new ContextAdapter());
        }
        return super.showAtLocation(parent, gravity, x, y);
    }

    private class ContextAdapter extends BaseAdapter<ContextMenu, ItemContextMenuBinding> {

        @Override
        protected int onResolveNormalTypeLayoutId() {
            return R.layout.item_context_menu;
        }

        @Override
        protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemContextMenuBinding binding, int position, ContextMenu data, @NonNull List<Object> payloads) {

        }
    }
}
