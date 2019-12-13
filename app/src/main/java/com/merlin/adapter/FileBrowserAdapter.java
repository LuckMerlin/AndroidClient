package com.merlin.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.client.databinding.ItemListFileBinding;
import com.merlin.debug.Debug;
import com.merlin.util.FileSize;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FileBrowserAdapter extends BaseAdapter<FileMeta, ItemListFileBinding>  {

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemListFileBinding binding, int position, FileMeta data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data){
            setText(binding.itemListFileTitle,data.getName(),"None");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sub=data.isDirectory()?"("+data.getLength()+")":" "+data.getExtension();
            sub+=" "+ FileSize.formatSizeText(data.getSize());
            sub+=" "+sdf.format(new Date((long)data.getLastModifyTime()));
            setText(binding.itemListFileSub,sub,null);
        }
    }
}
