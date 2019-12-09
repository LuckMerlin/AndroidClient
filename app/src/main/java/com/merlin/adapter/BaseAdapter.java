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

import com.merlin.bean.File;
import com.merlin.client.R;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseAdapter<T,V extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_TAIL = 2;
    public static final int TYPE_EMPTY = 3;
    private WeakReference<OnItemClickListener> mItemClickListener;
    private Handler mHandler;
    private List<T> mData;

    public interface OnItemClickListener{
        void onItemClick(View view, File bean);
    }


    public final void setData(List<T> data,boolean notify){
        mData= data;
        if (notify){
            if (Looper.getMainLooper() == Looper.myLooper()){
                notifyDataSetChanged();
            }else{
                mHandler=null==mHandler?new Handler(Looper.getMainLooper()):mHandler;
                mHandler.post(()->notifyDataSetChanged());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener ){
        mItemClickListener=null!=listener?new WeakReference<>(listener):null;
    }


    public final OnItemClickListener getOnItemClickListener(){
        WeakReference<OnItemClickListener>  reference= mItemClickListener;
        return null!=reference?reference.get():null;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      final LayoutInflater in=LayoutInflater.from(parent.getContext());
      switch (viewType){
          case TYPE_NORMAL:
              return new ViewHolder(DataBindingUtil.inflate(in,R.layout.item_list_file, parent, false));
          case TYPE_TAIL:
              return new BaseViewHolder(in.inflate(R.layout.list_tail, parent, false));
        }
       return new BaseViewHolder(in.inflate(R.layout.list_empty, parent, false));
    }

    public final T getItem(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

    @Override
    public int getItemViewType(int position) {
        List<T> data=mData;
        int size=null!=data?data.size():0;
        if (size>0&&position>=0) {
            return position == size+1?TYPE_TAIL:TYPE_NORMAL;
        }
        return TYPE_EMPTY;
    }

    @Override
    public final int getItemCount() {
        List<T> data=mData;
        return 2+(null!=data?data.size():0);
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
}
