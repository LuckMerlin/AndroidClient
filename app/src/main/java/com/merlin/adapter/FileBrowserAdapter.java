package com.merlin.adapter;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.File;
import com.merlin.client.databinding.ItemListFileBinding;


public class FileBrowserAdapter extends BaseAdapter<File, ItemListFileBinding>  {


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewDataBinding binding=null!=holder&&holder instanceof ViewHolder?((ViewHolder)holder).getBinding():null;
        ItemListFileBinding item=null!=binding&&binding instanceof ItemListFileBinding?(ItemListFileBinding)binding:null;
        File bean=null!=item?getItem(position):null;
        if (null!=item&&null!=bean){
            setText(item.itemListFileTitle,bean.getName(),"None");
            setText(item.itemListFileSub,bean.getExtension(),"None");
            item.getRoot().setOnClickListener((view)->{
                   OnItemClickListener listener=getOnItemClickListener();
                   if (null!=listener){
                       listener.onItemClick(view,bean);
                   }
            });
        }
    }
}
