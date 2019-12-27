package com.merlin.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemContextMenuBinding;

import java.util.List;

public class ContextMenuWindow extends PopupWindow{

    public ContextMenuWindow(boolean touchable){
        super(touchable);
    }

    @Override
    public final boolean setContentView(View view) {
        View curr=getContentView();
        if (null==view||(null!=curr&&curr instanceof RecyclerView)||!( view instanceof RecyclerView)){
            return false;
        }
        return super.setContentView(view);
    }

    public final void setOnItemClickListener(BaseAdapter.OnItemClickListener listener){
        ContextAdapter adapter=getAdapter();
        if (null!=adapter){
            adapter.setOnItemClickListener(listener);
        }
    }

    public void add(int ...textResIds){
        if (null!=textResIds&&textResIds.length>0){
            for (int id:textResIds){
                add(new ContextMenu(id));
            }
        }
    }

    public boolean add(ContextMenu menu){
        ContextAdapter adapter=getAdapter();
        if (null!=adapter&&null!=menu){
            return adapter.add(menu);
        }
        return false;
    }

    private ContextAdapter getAdapter(){
        RecyclerView recyclerView=getRecyclerView();
        RecyclerView.Adapter adapter=null!=recyclerView?recyclerView.getAdapter():null;
        return null!=adapter&&adapter instanceof ContextAdapter?((ContextAdapter)adapter):null;
    }

    private RecyclerView getRecyclerView(){
        View view=getContentView();
        if (null!=view&&view instanceof RecyclerView){
            return (RecyclerView)view;
        }
        return null;
    }

    @Override
    public final boolean showAtLocation(View parent, int gravity, int x, int y) {
        View view=getContentView();
        if (null==view||!(view instanceof RecyclerView)){
            Context context=parent.getContext();
            RecyclerView recyclerView=new RecyclerView(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new ContextAdapter());
            setContentView(recyclerView);
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
            if (null!=binding){
                binding.setMenu(data);
            }
        }
    }
}
