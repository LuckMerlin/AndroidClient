package com.merlin.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<T,V extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnLayoutManagerResolve {
    private List<T> mData;
    private Handler mHandler;
    public final static int TYPE_NONE=0;
    public final static int TYPE_TAIL=-1;
    public final static int TYPE_HEAD=-2;
    public final static int TYPE_EMPTY=-3;

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      final LayoutInflater in=LayoutInflater.from(parent.getContext());
//      switch (viewType){
//          case TYPE_NORMAL:
//              return new ViewHolder(DataBindingUtil.inflate(in,onResolveNormalTypeLayoutId(), parent, false));
//          case TYPE_TAIL:
//              return new BaseViewHolder(in.inflate(R.layout.list_tail, parent, false));
//        }
       return new BaseViewHolder(in.inflate(R.layout.list_empty, parent, false));
    }

    protected void onBindViewHolder(RecyclerView.ViewHolder holder,V binding,int position,T data, @NonNull List<Object> payloads){
        //Do nothing
    }

   @Override
   public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
      super.onBindViewHolder(holder, position, payloads);
      ViewDataBinding binding=null!=holder&&holder instanceof ViewHolder?((ViewHolder)holder).getBinding():null;
      T data=getItemData(position);
      onBindViewHolder(holder,(V)binding,position,data,payloads);
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
    public int getItemCount() {
        return getDataCount()+2;
    }

    protected int getItemViewType(int position,int size) {

        return TYPE_NONE;
    }

   @Override
  public final int getItemViewType(int position) {
        List<T> data=mData;
        int size=null!=data?data.size():0;
        return getItemViewType(position,size);
    }

  public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

  public final List<T> getData() {
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

  public void reset(T ...datas){
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

    public void reset(List<T> datas){
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

  protected final boolean replace(T data,int index){
        List<T> list=null!=data?mData:null;
        int size=null!=list?list.size():-1;
        if (null!=data&&index>=0&&index<=size){
            if (index<size){
                list.remove(index);
            }
            list.add(index,data);
            notifyItemChanged(index);
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

  protected final int index(T data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
   }

}
