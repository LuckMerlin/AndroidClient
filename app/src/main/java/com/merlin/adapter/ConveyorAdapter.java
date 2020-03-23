package com.merlin.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.conveyor.StatusTextFormat;
import com.merlin.transport.Convey;

import java.util.List;

public class ConveyorAdapter<T extends Convey> extends ListAdapter<T> implements OnItemTouchResolver {
    private final StatusTextFormat mFormat=new StatusTextFormat();

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_conveyor:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, T data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemConveyorBinding){
            ItemConveyorBinding itb=(ItemConveyorBinding)binding;
            View view=null!=holder?holder.itemView:null;
            Context context=null!=view?view.getContext():null;
            itb.setStatus(null!=context?mFormat.format(context,data,""):"");
            itb.setPosition(position+1);
            itb.setData(data);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(), RecyclerView.VERTICAL,false);
    }

    @Override
    public Object onResolveItemTouch(RecyclerView recyclerView) {
        return new ItemSlideRemover();
    }

}
