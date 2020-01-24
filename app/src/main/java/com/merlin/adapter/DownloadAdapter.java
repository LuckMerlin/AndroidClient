package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemTransportBinding;
import com.merlin.task.Download;

import java.util.List;

public class DownloadAdapter extends BaseAdapter<Download, ItemTransportBinding> {

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_transport;
    }

    public boolean update(Download task){
        int index=null!=task?index(task):-1;
        return index>=0? replace(task,index):add(task);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemTransportBinding binding, int position, Download data, @NonNull List<Object> payloads) {
        if (null!=data&&null!=binding){
            binding.setTask(data);
        }
    }
}
