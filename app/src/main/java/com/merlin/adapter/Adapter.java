package com.merlin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class Adapter<T> extends  RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnLayoutManagerResolve  {
    private List<T> mData;

    public  Adapter(T ...data){
        append(false,data);
    }

    public boolean empty(){
        List<T> data=mData;
        if (null!=data&&data.size()>0){
            data.clear();
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public synchronized final boolean replace(List<T> data,int from){
        int size=null!=data?data.size():-1;
        if (size>0&&from>=0){
            List<T> curr=mData;
            curr=null!=curr?curr:(mData=new ArrayList<>());
            synchronized (curr){
                int length=curr.size();
                if (length<from){
                    return false;
                }
                int index=0;
                boolean changed=false;
                for (int i = 0; i < size; i++) {
                    if ((index = from + i) < length) {
                        curr.remove(curr.get(index));
                        changed = true;
                    }
                    curr.add(index, data.get(i));
                    if (changed) {
                        notifyItemChanged(index);
                    }else{
                        notifyItemInserted(index);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final boolean setData(Collection<T> data){
        boolean changed=false;
        List<T> current=mData;
        if (null!=current){
            current.clear();
            mData=null;
            changed=true;
        }
        int size=null!=data?data.size():0;
        if (size>0){
            (mData=new ArrayList<>(size)).addAll(data);
            changed=true;
            notifyDataSetChanged();
        }
        return changed;
    }

    public final boolean append(boolean notify, Collection<T> data){
        if (null!=data&&data.size()>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            for (T child:data) {
                if (null!=child&&list.add(child)&&notify){
                    notifyItemChanged(list.size());
                }
            }
            return true;
        }
        return false;
    }

    public final boolean remove(List<T> datas,String debug){
        int count=null!=datas?datas.size():-1;
        if (count>0){
            for (T data:datas) {
                remove(data,debug);
            }
            return true;
        }
        return false;
    }

    public final boolean remove(T data,String debug){
        List<T> datas=null!=data?mData:null;
        int index=null!=datas?datas.indexOf(data):-1;
        if (index>=0&&datas.remove(data)){
            notifyItemRemoved(index);
            return true;
        }
        return false;
    }

    public final boolean append(boolean notify,T ...data){
        if (null!=data&&data.length>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            for (T child:data) {
                if (null!=child&&list.add(child)&&notify){
                    notifyItemChanged(list.size());
                }
            }
            return true;
        }
        return false;
    }

    public final T getItem(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

    protected Integer onResolveItemLayoutId(ViewGroup parent,int viewType){
        //Do nothing
        return null;
    }

    public final List<T> getData() {
        List<T> data=mData;
        int size=null!=data?data.size():-1;
        if (size>0){
           List<T> result=new ArrayList<>(size);
           result.addAll(data);
           return result;
        }
        return null;
    }

    public final int getDataSize() {
        List<T> data=mData;
        return null!=data?data.size():-1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        final LayoutInflater in=LayoutInflater.from(context);
        Integer layoutId=onResolveItemLayoutId(parent,viewType);
        View root=null;
        if (null!=layoutId){
            ViewDataBinding binding=DataBindingUtil.inflate(in,layoutId,parent,false);
            if (null!=binding){
                return new ViewHolder<>(binding);
            }else{
                root=in.inflate(layoutId,parent,false);
            }
        }
        return new ViewHolder(null!=root?root:new View(context));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

    protected void onBindViewHolder(RecyclerView.ViewHolder holder,ViewDataBinding binding,int position,T data, @NonNull List<Object> payloads){
        //Do nothing
    }

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ViewDataBinding binding = null != holder && holder instanceof ViewHolder ? ((ViewHolder) holder).getBinding() : null;
        T data = getItem(position);
        onBindViewHolder(holder, binding, position, data, payloads);
    }

    public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

    @Override
    public int getItemCount() {
        return getDataCount();
    }

    protected final int index(T data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
    }

    protected static final class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder{
        private final V mBinding;

        protected ViewHolder(V binding){
              super(null!=binding?binding.getRoot():null);
              mBinding=binding;
        }

        protected ViewHolder(View root){
            super(root);
            mBinding=null;
        }

        public V getBinding() {
            return mBinding;
        }
    }

}
