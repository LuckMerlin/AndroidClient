//package com.merlin.model;
//
//import android.content.Context;
//
//import androidx.annotation.ColorInt;
//import androidx.databinding.ViewDataBinding;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.merlin.adapter.BaseAdapter;
//import com.merlin.debug.Debug;
//import com.merlin.view.DataListLayout;
//
//import java.util.List;
//
//
//public class DataListModel<T> extends BaseModel {
//    private final BaseAdapter<T, ViewDataBinding> mAdapter;
//    private final RecyclerView.LayoutManager mLayoutManager;
//    private final RecyclerView.ItemDecoration mItemDecoration;
//    private DataListLayout.Bridge mBridge;
//
//    public DataListModel(Context context, BaseAdapter adapter, LinearLayoutManager manager) {
//        this(context,adapter,manager,null);
//    }
//
//    public DataListModel(Context context, BaseAdapter adapter, LinearLayoutManager manager, RecyclerView.ItemDecoration decoration){
//        super(context);
//        mAdapter=adapter;
//        mLayoutManager=manager;
//        mItemDecoration=decoration;
//        if (null!=adapter){
//            if (this instanceof BaseAdapter.OnItemClickListener) {
//                adapter.setOnItemClickListener((BaseAdapter.OnItemClickListener) this);
//            }
//            if (this instanceof BaseAdapter.OnItemLongClickListener){
//                adapter.setOnItemLongClickListener((BaseAdapter.OnItemLongClickListener)this);
//            }
//            if (this instanceof BaseAdapter.OnItemMultiClickListener){
//                adapter.setOnItemMultiClickListener((BaseAdapter.OnItemMultiClickListener)this);
//            }
//        }
//    }
//
//    public final boolean addData(List<T> data){
//        BaseAdapter adapter=mAdapter;
//        if (null!=adapter&&null!=data&&data.size()>0){
//            adapter.add(data);
//            return true;
//        }
//        return false;
//    }
//
//    public final boolean setData(List data, boolean notify){
//        BaseAdapter adapter=mAdapter;
//        if (null!=adapter){
//            adapter.setData(data,notify);
//            return true;
//        }
//        return false;
//    }
//
//    public void onBridgeBoundChange(boolean bound){
//        //Do nothing
//    }
//
//    public final boolean setRefreshing(boolean refreshing){
//        DataListLayout.Bridge bridge=mBridge;
//        if (null!=bridge){
//            bridge.setRefreshing(refreshing);
//            return true;
//        }
//        return false;
//    }
//
//    public final boolean setColorSchemeColors(int... colors){
//        DataListLayout.Bridge bridge=mBridge;
//        if (null!=bridge){
//            bridge.setColorSchemeColors(colors);
//            return true;
//        }
//        return false;
//    }
//
//    public final void bindBridge(DataListLayout.Bridge bridge){
//            mBridge=bridge;
//            onBridgeBoundChange(null!=bridge);
//    }
//
//    public final boolean setProgressBackgroundColorSchemeColor(@ColorInt int color){
//        DataListLayout.Bridge bridge=mBridge;
//        if (null!=bridge){
//            bridge.setProgressBackgroundColorSchemeColor(color);
//            return true;
//        }
//        return false;
//    }
//
//    public RecyclerView.ItemDecoration getItemDecoration() {
//        return mItemDecoration;
//    }
//
//    public final BaseAdapter<T,ViewDataBinding> getAdapter() {
//        return mAdapter;
//    }
//
//    public final RecyclerView.LayoutManager getLayoutManager() {
//        return mLayoutManager;
//    }
//
//}
