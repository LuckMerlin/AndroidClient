package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.client.databinding.ItemTransportBinding;
import com.merlin.task.DownloadTask;

import java.util.List;

public class DownloadAdapter extends BaseAdapter<DownloadTask, ItemTransportBinding> {


    @Override
    protected int onResolveNormalTypeLayoutId() {
        return R.layout.item_transport;
    }

    public boolean update(DownloadTask task){
        int index=null!=task?index(task):-1;
        if (index>=0){
            return replace(task,index);
        }
        return false;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemTransportBinding binding, int position, DownloadTask data, @NonNull List<Object> payloads) {
            if (null!=data&&null!=binding){
                binding.setTask(data);
            }
    }
}
