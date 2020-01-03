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

public abstract class BaseAdapter<T,V extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_TAIL = 2;
    public static final int TYPE_EMPTY = 3;
    private WeakReference<View.OnClickListener> mViewClickListener;
    private WeakReference<OnItemClickListener> mClickListener;
    private WeakReference<OnItemLongClickListener> mLongClickListener;
    private WeakReference<OnItemMultiClickListener> mMultiClickListener;
    private Handler mHandler;
    private List<T> mData;


    public interface OnItemClickListener<T>{
        void onItemClick(View view,int sourceId,int position, T data);
    }

    public interface OnItemMultiClickListener<T>{
        boolean onItemMultiClick(View view,int clickCount,int sourceId,int position, T data);
    }

    public interface OnItemLongClickListener<T>{
        boolean onItemLongClick(View view,int sourceId,int position, T data);
    }

    public final void setData(List<T> data){
         setData(data,true);
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

    public void setOnItemMultiClickListener(OnItemMultiClickListener listener){
        mMultiClickListener=null!=listener?new WeakReference<>(listener):null;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mClickListener=null!=listener?new WeakReference<>(listener):null;
    }

    public void setViewClickListener(View.OnClickListener listener){
        mViewClickListener=null!=listener?new WeakReference<>(listener):null;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        mLongClickListener=null!=listener?new WeakReference<>(listener):null;
    }

    protected abstract int onResolveNormalTypeLayoutId();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      final LayoutInflater in=LayoutInflater.from(parent.getContext());
      switch (viewType){
          case TYPE_NORMAL:
              return new ViewHolder(DataBindingUtil.inflate(in,onResolveNormalTypeLayoutId(), parent, false));
          case TYPE_TAIL:
              return new BaseViewHolder(in.inflate(R.layout.list_tail, parent, false));
        }
       return new BaseViewHolder(in.inflate(R.layout.list_empty, parent, false));
    }

    protected abstract void onBindViewHolder(RecyclerView.ViewHolder holder,V binding,int position,T data, @NonNull List<Object> payloads);

    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ViewDataBinding binding=null!=holder&&holder instanceof ViewHolder?((ViewHolder)holder).getBinding():null;
        T data=getItem(position);
        onBindViewHolder(holder,(V)binding,position,data,payloads);
        View root=null!=binding?binding.getRoot():null;
        if (null!=root){
                WeakReference<OnItemClickListener> reference=mClickListener;
                OnItemClickListener listener=null!=reference?reference.get():null;
                WeakReference<OnItemMultiClickListener> doubleReference=mMultiClickListener;
                final OnItemMultiClickListener multiListener=
                        null!=doubleReference?doubleReference.get():null;
                if (null!=listener||null!=multiListener){
                    final int maxInterval=400;
                    final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                        @Override
                        public void run() {
                            int count=mClickCount;
                            mClickCount=0;//Reset
                            if (null==multiListener||!multiListener.
                                    onItemMultiClick(mView,count, getViewId(),position,data)){
                                if (count==1&&null!=listener){
                                    listener.onItemClick(mView, mView.getId(),position, data);
                                }
                            }
                        }
                    };
                    root.setOnClickListener((view)->{
                        if (null==multiListener&&null!=listener){
                            listener.onItemClick(view, view.getId(),position, data);
                        }else if(null!=multiListener){
                            view.removeCallbacks(multiRunnable);
                            multiRunnable.mView=view;
                            if (multiRunnable.mClickCount==0){
                                multiRunnable.mFirstTime=System.currentTimeMillis();
                            }
                            multiRunnable.mClickCount+=1;
                            view.postDelayed(multiRunnable,maxInterval);
                        }
                    });
                }
                WeakReference<OnItemLongClickListener> longReference=mLongClickListener;
                OnItemLongClickListener longClickListener=null!=longReference?longReference.get():null;
                if (null!=listener){
                    root.setOnLongClickListener((view)->null!=longClickListener&& longClickListener.onItemLongClick(view, view.getId(),position,data));
                }
                Class cls=binding.getClass();
                if (null!=cls){
                    try {
                        Method method=cls.getDeclaredMethod("setClickListener", View.OnClickListener.class);
                        if (null!=method){
                            method.setAccessible(true);
                            WeakReference<View.OnClickListener> viewClickReference=mViewClickListener;
                            View.OnClickListener viewClickListener=null!=viewClickReference?viewClickReference.get():null;
                            method.invoke(binding,viewClickListener);
                        }
                    } catch (Exception e) {
                        //Do nothing
                    }
                }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Do nothing
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
            return position == size?TYPE_TAIL:TYPE_NORMAL;
        }
        return TYPE_EMPTY;
    }

    public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

    @Override
    public final int getItemCount() {
        List<T> data=mData;
        return 1+(null!=data?data.size():0);
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
                if (!list.contains(data)&&list.add(data)){
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

  private static abstract class MultiClickRunnable implements Runnable{
         View mView;
         long mFirstTime;
         int mClickCount;

      int getViewId(){
          View view=mView;
          return null!=view?view.getId():-1;
      }
    }

}
