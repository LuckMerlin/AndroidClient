package com.merlin.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.client.databinding.ItemListFileBinding;

import java.util.List;


public class FileBrowserAdapter extends BaseAdapter<FileMeta, ItemListFileBinding>  {

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemListFileBinding binding, int position, FileMeta data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data){
            setText(binding.itemListFileTitle,data.getName(),"None");
            setText(binding.itemListFileSub,data.getExtension(),"d");
        }
    }
}
