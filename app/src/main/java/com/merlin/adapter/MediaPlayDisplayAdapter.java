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

public class MediaPlayDisplayAdapter extends Adapter<Integer> {
    private final PagerSnapHelper mHelper=new PagerSnapHelper();

    public MediaPlayDisplayAdapter(){
        super( R.layout.media_display_sheet_category);
    }
//    private final int[] mLayoutIds=new int[]{
////                R.layout.media_display_all_medias
////                ,
////            R.layout.media_display_play
////                ,
//            R.layout.media_display_sheet_category
//    };


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
    public int getItemViewType(int position) {
        Integer data=getItem(position);
        return null!=data?data:-1;
    }


    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        if (null!=rv){
            LinearLayoutManager manager=new LinearLayoutManager(rv.getContext(),LinearLayoutManager.HORIZONTAL,false);
//            mHelper.attachToRecyclerView(rv);
            return manager;
        }
        return null;
    }


}
