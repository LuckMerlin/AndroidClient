package com.merlin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.debug.Debug;

public class MediaPlayDisplayAdapter extends Adapter {
    private final PagerSnapHelper mHelper=new PagerSnapHelper();
    private final int[] mLayoutIds=new int[]{
//                R.layout.media_display_all_medias
//                ,
            R.layout.media_display_play
//                , R.layout.media_display_sheets
    };


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        final LayoutInflater in=LayoutInflater.from(context);
        ViewDataBinding binding= DataBindingUtil.inflate(in,viewType, parent, false);
//        if (null!=binding){
//            if(binding instanceof MediaDisplaySheetsBinding){
//                MediaDisplaySheetsModel model=new MediaDisplaySheetsModel(context);
//                model.setRootView(binding.getRoot());
//                ((MediaDisplaySheetsBinding)binding).setVm(model);
//            }else if (binding instanceof MediaDisplayAllMediasBinding){
//                MediaDisplayAllMediasModel model=new MediaDisplayAllMediasModel(context);
//                model.setRootView(binding.getRoot());
//                ((MediaDisplayAllMediasBinding)binding).setVm(model);
//            }else if (binding instanceof MediasAllBinding){
//
//            }
//                binding.setVariable(com.merlin.client.BR.vm,mModel);
//        }
        return new ViewHolder(null!=binding?binding.getRoot():null);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return null!=mLayoutIds?mLayoutIds.length:0;
    }

    @Override
    public int getItemViewType(int position) {
        int[] data=mLayoutIds;
        return data[position];
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            LinearLayoutManager manager=new LinearLayoutManager(rv.getContext(),LinearLayoutManager.HORIZONTAL,false);
            mHelper.attachToRecyclerView(rv);
            rv.setLayoutManager(manager);
            return manager;
        }
        return null;
    }

    private final static class ViewHolder extends RecyclerView.ViewHolder{
        protected ViewHolder(View root){
            super(root);
        }
    }

}
