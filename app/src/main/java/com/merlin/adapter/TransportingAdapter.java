package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.databinding.ItemTransportingBinding;
import com.merlin.transport.Transport;

import java.util.List;

public class TransportingAdapter extends BaseAdapter<Transport, ItemTransportingBinding> {

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemTransportingBinding binding, int position, Transport data, @NonNull List<Object> payloads) {
            if (null!=data&&null!=binding){
                binding.setTransport(data);
            }
    }
}
