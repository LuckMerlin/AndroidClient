package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemTransportBinding;
import com.merlin.transport.Transport;

import java.util.List;

public class TransportAdapter extends BaseAdapter<Transport, ItemTransportBinding> {


    @Override
    protected int onResolveNormalTypeLayoutId() {
        return R.layout.item_transport;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemTransportBinding binding, int position, Transport data, @NonNull List<Object> payloads) {
            if (null!=data&&null!=binding){
                binding.setTransport(data);
            }
    }
}
