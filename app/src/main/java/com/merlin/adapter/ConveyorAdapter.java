package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemConveyorBinding;
import com.merlin.client.databinding.ItemTransportBinding;
import com.merlin.transport.AbsTransport;
import com.merlin.transport.Convey;

import java.util.List;

public class ConveyorAdapter<T extends Convey> extends Adapter<T> implements OnItemTouchResolver {

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_conveyor;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, T data, @NonNull List<Object> payloads) {
        if (null!=binding&&binding instanceof ItemConveyorBinding){
            ItemConveyorBinding itb=(ItemConveyorBinding)binding;
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
