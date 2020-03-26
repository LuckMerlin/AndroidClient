package com.merlin.adapter;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;
import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnLayoutManagerResolve {
    private ArrayList<T> mData;
    private Handler mHandler;
    public final static int TYPE_NONE=0;
    public final static int TYPE_TAIL=-1;
    public final static int TYPE_EMPTY=-3;
    public final static int TYPE_DATA=-4;

    public ListAdapter(){
        this(null);
    }

    public ListAdapter(ArrayList<T>  list){
        if (null!=list&&list.size()>0) {
            add(list,true,"");
        }
    }

    protected Integer onResolveViewTypeLayoutId(int viewType){
        return null;
    }

    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater in,int viewType){
        return null;
    }

    @Override
  public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      final LayoutInflater in=LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder=onCreateViewHolder(in,viewType);
        if (viewHolder ==null){
           Integer integer= onResolveViewTypeLayoutId(viewType);
           ViewDataBinding binding=null!=integer&&integer!= Resources.ID_NULL?DataBindingUtil.inflate(in,integer,parent,false):null;
           viewHolder= null!=binding?new ViewHolder(binding):null;
        }
        if (null==viewHolder){
            switch (viewType){
                case TYPE_TAIL:
                    viewHolder= new BaseViewHolder(in.inflate(R.layout.list_tail, parent, false));
                    break;
                case TYPE_EMPTY:
                    viewHolder= new BaseViewHolder(in.inflate(R.layout.list_empty, parent, false));
                    break;
                default:
                    viewHolder= new BaseViewHolder(new View(parent.getContext()));
                    break;
            }
        }
       return viewHolder;
    }

  protected void onBindViewHolder(RecyclerView.ViewHolder holder,int viewType,ViewDataBinding binding,int position,T data, @NonNull List<Object> payloads){
        //Do nothing
  }

   @Override
  public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
      super.onBindViewHolder(holder, position, payloads);
      ViewDataBinding binding=null!=holder&&holder instanceof ViewHolder?((ViewHolder)holder).getBinding():null;
      T data=getItemData(position);
      onBindViewHolder(holder,null!=holder?holder.getItemViewType():TYPE_NONE,binding,position,data,payloads);
       if (null==payloads||payloads.isEmpty()) {
//           onBindViewHolder(holder, position);
       } else {
//           View itemView=holder.itemView;
//           if (null!=itemView){
//               itemView.setVisibility(((Item)payloads.get(0)).disabled ? View.VISIBLE : View.INVISIBLE);
//           }
       }
   }

    @Override
  public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

  public final T getItemData(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

    @Override
  public final int getItemCount() {
        int dataCount=getDataCount();
        return (dataCount<=0?0:dataCount)+1;
    }

  protected int getItemViewType(int position,int size) {
        return TYPE_DATA;
    }

   @Override
  public final int getItemViewType(int position) {
       List<T> data=mData;
       int size=null!=data?data.size():0;
       if (size==0){
           return TYPE_EMPTY;
       }
       if (position == size){
           return TYPE_TAIL;
       }
       return getItemViewType(position,size);
    }

  public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

  public final boolean clean(){
        List<T> data=mData;
        int size=null!=data?data.size():0;
        if (size>0){
            data.clear();
            notifyItemRangeRemoved(0,size);
            return true;
        }
        return false;
  }

  public final ArrayList<T> getData() {
      ArrayList<T> data=mData;
      int length=null!=data?data.size():0;
      ArrayList<T> result=length>0?new ArrayList<>(length):null;
      return null!=result&&result.addAll(data)?result:null;
    }

  protected final static class BaseViewHolder extends RecyclerView.ViewHolder{
        protected BaseViewHolder(View root){
            super(root);
        }
   }

  protected final static class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder{
    private V mBinding;

    protected ViewHolder(V binding){
        super(null!=binding?binding.getRoot():null);
        mBinding=binding;
    }

    public V getBinding() {
        return mBinding;
    }
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

  public boolean set(List<T> data,String debug){
        final List<T> list=mData;
        if (null!=data&&data.size()>0){
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }else if(null!=list){
            list.clear();
            mData=null;
            notifyDataSetChanged();
            return true;
        }
      return false;
  }

  public final boolean add(T data,boolean exceptExist,String debug){
     List<T> list=null!=data?new ArrayList<>():null;
    return null!=list&&list.add(data)&&add(list,exceptExist,debug);
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

  public final boolean replace(T data,String debug){
       List<T> list= null!=data?mData:null;
       int index=null!=list?list.indexOf(data):-1;
       return index>=0&&replace(index,data,debug);
   }

  public final boolean replace(int index,T data,String debug){
        if (null!=data&&index>=0){
            ArrayList<T> list= new ArrayList<>(1);
            list.add(data);
            return replace(index,list,debug);
        }
        return false;
    }

    public final boolean replace(int from,ArrayList<T> data,String debug){
        int length=null!=data?data.size():-1;
        if (length>0){
            ArrayList<T> list=mData;
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

    public final boolean isExist(Object data){
        return null!=data&&index(data)>=0;
    }

  public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder,View view,ViewDataBinding binding){
        //Do nothing
    }

  @Override
  public final void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (null!=holder){
            View view=holder.itemView;
            ViewDataBinding binding=null!=view? DataBindingUtil.getBinding(view):null;
            onViewDetachedFromWindow(holder,view,binding);
        }
    }
}
