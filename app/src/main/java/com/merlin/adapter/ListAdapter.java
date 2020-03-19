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
import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnLayoutManagerResolve {
    private List<T> mData;
    private Handler mHandler;
    public final static int TYPE_NONE=0;
    public final static int TYPE_TAIL=-1;
    public final static int TYPE_HEAD=-2;
    public final static int TYPE_EMPTY=-3;
    public final static int TYPE_DATA=-4;

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
   }

    @Override
  public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

  public final T getItemData(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

  protected int onIncreaseItemCount(int dataCount){
        return 0;
  }

    @Override
  public final int getItemCount() {
        int dataCount=getDataCount();
        int count=onIncreaseItemCount(dataCount);
        return (dataCount>0?2:1)+(count>=0?count:0);
    }

  protected int getItemViewType(int position,int size) {
        //Do nothing
        return TYPE_DATA;
    }

   @Override
  public final int getItemViewType(int position) {
       List<T> data=mData;
       int size=null!=data?data.size():0;
       if (size==0){
           return TYPE_EMPTY;
       }
       if (position ==0){
           return TYPE_HEAD;
       }else if (position == size){
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

  public final void setData(List<T> data){
        setData(data,true);
    }

  public final void addAll(List<T> data,boolean notify){
        if (null!=data&&data.size()>0){
            List<T> datas=mData=null!=mData?mData:new ArrayList<>();
            datas.addAll(data);
        }
        if (notify){
            if (Looper.getMainLooper() == Looper.myLooper()){
                notifyDataSetChanged();
            }else{
                mHandler=null==mHandler?new Handler(Looper.getMainLooper()):mHandler;
                mHandler.post(()->notifyDataSetChanged());
            }
        }
    }

  public final void setData(List<T> data,boolean notify){
        List<T> datas=mData=null!=mData?mData:new ArrayList<>();
        datas.clear();
        if (null!=data&&data.size()>0){
            datas.addAll(data);
        }
        if (notify){
            if (Looper.getMainLooper() == Looper.myLooper()){
                notifyDataSetChanged();
            }else{
                mHandler=null==mHandler?new Handler(Looper.getMainLooper()):mHandler;
                mHandler.post(()->notifyDataSetChanged());
            }
        }
    }

  private final List<T> getData() {
        return mData;
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

  protected final void setText(TextView tv,String value,String ifNull){
        if (null!=tv){
            tv.setText(null!=value?value:(null==ifNull?"":ifNull));
        }
  }

  public final void reset(T ...datas){
        List<T> list=mData;
        if (null==list){
            mData=list=new ArrayList<>();
        }else{
            list.clear();
            notifyDataSetChanged();
        }
        if (null!=datas&&datas.length>0){
           add(datas);
        }
    }

  public final void reset(List<T> datas){
        List<T> list=mData;
        if (null==list){
            mData=list=new ArrayList<>();
        }else{
            list.clear();
        }
        if (null!=datas&&datas.size()>0) {
            list.addAll(datas);
        }
        notifyDataSetChanged();
    }

  public final boolean add(T ...datas){
        if (null!=datas&&datas.length>0){
            List<T> list=mData;
            if (null==list){
                mData=list=new ArrayList<>();
            }
            for (T data:datas){
                if (data instanceof Collection){
                    for (Object child:(Collection)data){
                        if (null!=child){
                            add((T)child);
                        }
                    }
                    continue;
                }
                if (null!=data&&!list.contains(data)&&list.add(data)){
                    notifyItemInserted(list.indexOf(data));
                }
            }
            return true;
        }
      return false;
  }

  public final boolean replace(int from,List<T> data){
        List<T> list=null!=data?mData:null;
        int size=null!=list?list.size():-1;
        if (null!=data&&from>=0&&from<=size){
//            list.add(index,data);
//            notifyItemChanged(index);
            return true;
        }
        return false;
  }

  public final boolean remove(T data){
        List<T> list=mData;
        int index=null!=list&&null!=data?list.indexOf(data):-1;
        if (index>=0&&list.remove(data)){
            notifyItemRemoved(index);
            return true;
        }
        return false;
    }

  public final T remove(int index){
        List<T> list=mData;
        int size=null!=list?list.size():-1;
        if (index>=0&&index<=size){
            T removed=index<size?list.remove(index):null;
            if (null!=removed){
                notifyItemChanged(index);
            }
            return removed;
        }
        return null;
    }

  public final int index(T data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
   }

}
