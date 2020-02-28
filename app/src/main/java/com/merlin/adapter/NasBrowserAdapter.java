package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.NasFile;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemListFileBinding;

import java.util.List;


public abstract class NasBrowserAdapter extends BrowserAdapter<NasFile>{
//
//    @Override
//    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
//        return R.layout.item_list_file;
//    }
//
//    @Override
//    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, NasFile data, @NonNull List<Object> payloads) {
//        if (null!=binding&&null!=data&&binding instanceof ItemListFileBinding){
//            ItemListFileBinding itemBinding=(ItemListFileBinding)binding;
//            boolean multiChoose=isMultiChoose();
//            itemBinding.setIsChoose(isChoose(data));
//            itemBinding.setIsMultiChoose(multiChoose);
//            itemBinding.setMeta(data);
//            itemBinding.setPosition(position);
//        }
//    }

}
