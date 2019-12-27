package com.merlin.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemContextMenuBinding;

import java.util.ArrayList;
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

    public final void add(int ...textResIds){
        if (null!=textResIds&&textResIds.length>0){
            List<ContextMenu> list=new ArrayList<>();
            for (int id:textResIds){
                list.add(new ContextMenu(id));
            }
            if (null!=list&&list.size()>0) {
                add(list.toArray(new ContextMenu[list.size()]));
            }
        }
    }

    public final void reset(int ...textResIds){
        if (null!=textResIds&&textResIds.length>0){
            List<ContextMenu> list=new ArrayList<>();
            for (int id:textResIds){
                list.add(new ContextMenu(id));
            }
            ContextAdapter adapter=getAdapter();
            adapter.reset(list);
        }
    }

    public final boolean add(ContextMenu ...menus){
        ContextAdapter adapter=getAdapter();
        return null!=adapter&&null!=menus&&menus.length>0&&adapter.add(menus);
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
