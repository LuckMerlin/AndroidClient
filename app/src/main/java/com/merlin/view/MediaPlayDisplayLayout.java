package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.MediaDisplayAllMediasBinding;
import com.merlin.client.databinding.MediaDisplaySheetsBinding;
import com.merlin.client.databinding.MediasAllBinding;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;
import com.merlin.model.MediaDisplayAllMediasModel;
import com.merlin.model.MediaDisplaySheetsModel;

public class MediaPlayDisplayLayout extends RecyclerView implements BaseModel.OnModelViewClick {

    public MediaPlayDisplayLayout(@NonNull Context context) {
        this(context, null);
    }

    public MediaPlayDisplayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaPlayDisplayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LinearLayoutManager manager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        PagerSnapHelper helper=new PagerSnapHelper();
        helper.attachToRecyclerView(this);
        setLayoutManager(manager);
        setAdapter(new Adapter());
        scrollToPosition(2);
    }

    @Override
    public void onViewClick(View v, int id,Object obj) {
//          int count=getChildCount();
//          for (int i=0;i< count;i++){
//              View child=getChildAt(i);
//              ViewDataBinding binding=null!=child?DataBindingUtil.getBinding(child):null;
//              if (null!=binding){
//                 MediaDisplayAllMediasModel model= binding instanceof MediaDisplayAllMediasBinding?((MediaDisplayAllMediasBinding)binding).getVm():null;
//                  if (null!=model&&model instanceof BaseModel.OnModelViewClick){
//                      ((BaseModel.OnModelViewClick)model).onViewClick(v,id,obj);
//                  }
//                  MediaDisplaySheetsModel sheetModel= binding instanceof MediaDisplaySheetsBinding?((MediaDisplaySheetsBinding)binding).getVm():null;
//                  if (null!=sheetModel&&sheetModel instanceof BaseModel.OnModelViewClick){
//                      ((BaseModel.OnModelViewClick)sheetModel).onViewClick(v,id,obj);
//                  }
//              }
//          }
    }

    private final class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private final int[] mLayoutIds=new int[]{
//                R.layout.media_display_all_medias
//                ,
                R.layout.media_display_play
//                , R.layout.media_display_sheets
        };

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context=parent.getContext();
            final LayoutInflater in=LayoutInflater.from(context);
            ViewDataBinding binding=DataBindingUtil.inflate(in,viewType, parent, false);
            if (null!=binding){
                 if(binding instanceof MediaDisplaySheetsBinding){
                     MediaDisplaySheetsModel model=new MediaDisplaySheetsModel(context);
                     model.setRootView(binding.getRoot());
                     ((MediaDisplaySheetsBinding)binding).setVm(model);
                 }else if (binding instanceof MediaDisplayAllMediasBinding){
                     MediaDisplayAllMediasModel model=new MediaDisplayAllMediasModel(context);
                     model.setRootView(binding.getRoot());
                     ((MediaDisplayAllMediasBinding)binding).setVm(model);
                 }else if (binding instanceof MediasAllBinding){

                 }
//                binding.setVariable(com.merlin.client.BR.vm,mModel);
            }
            return new BaseViewHolder(null!=binding?binding.getRoot():null);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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

    }

    private final static class BaseViewHolder extends RecyclerView.ViewHolder{
        protected BaseViewHolder(View root){
            super(root);

        }
    }

}
