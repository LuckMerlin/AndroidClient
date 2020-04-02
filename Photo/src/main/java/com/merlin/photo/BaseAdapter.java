package com.merlin.photo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

abstract class BaseAdapter<T,VH extends BaseAdapter.ViewHolder>extends RecyclerView.Adapter<VH> {
    private List<T> mData;

    public BaseAdapter(){
        this(null);
    }

    public BaseAdapter(List<T> data){
        mData=data;
    }

    public final boolean add(List<T> data,boolean exceptExist,String debug){
        if (null!=data&&data.size()>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            synchronized (list){
                for (T child:data) {
                    if (null==child||(list.contains(child)&&exceptExist)||list.add(child)){
                        continue;
                    }
                    notifyItemInserted(list.size()-1);
                }
            }
            return true;
        }
        return false;
    }

    public final boolean replace(int from,List<T> data,String debug){
        int length=null!=data?data.size():-1;
        if (length>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            if (null!=list){
                int size=list.size();
                from=from<0||from>size?size:from;
                synchronized (list){
                    for (int i = 0; i < length; i++) {
                        int index=i+from;
                        T child=data.get(i);
                        if (index<list.size()){
                            list.remove(index);
                            list.add(child);
                            notifyItemChanged(index);
                        }else{
                            list.add(child);
                            notifyItemInserted(index);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final T remove(Object data,String debug){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                T indexData=index>=0?list.get(index):null;
                if (null!=indexData&&list.remove(indexData)){
                    notifyItemRemoved(index);
                    if (list.size()==0){
                        mData=null;
                    }
                    return indexData;
                }
            }
        }
        return null;
    }

    public final boolean isExist(Object data){
        return null!=data&&index(data)>=0;
    }

    public final int index(Object data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
    }

    public final T get(Object data){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                return index>=0?list.get(index):null;
            }
        }
        return null;
    }

    public static class ViewDataBindingHolder extends RecyclerView.ViewHolder{
        public ViewDataBindingHolder(ViewDataBinding binding){
            super(null!=binding?binding.getRoot():null);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
