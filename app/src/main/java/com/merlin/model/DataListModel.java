package com.merlin.model;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.view.DataListLayout;

import java.util.List;


public class DataListModel extends BaseModel {
    private final BaseAdapter mAdapter;
    private final RecyclerView.LayoutManager mLayoutManager;
    private DataListLayout.Bridge mBridge;

    public DataListModel(Context context,BaseAdapter adapter, LinearLayoutManager manager){
        super(context);
        mAdapter=adapter;
        mLayoutManager=manager;
        if (null!=adapter){
            adapter.setOnItemClickListener();
        }
    }

    public final boolean setData(List data, boolean notify){
        BaseAdapter adapter=mAdapter;
        if (null!=adapter){
            adapter.setData(data,notify);
            return true;
        }
        return false;
    }

    public void onBridgeBoundChange(boolean bound){
        //Do nothing
    }

    public final boolean setRefreshing(boolean refreshing){
        DataListLayout.Bridge bridge=mBridge;
        if (null!=bridge){
            bridge.setRefreshing(refreshing);
        }
        return false;
    }

    public final boolean setColorSchemeColors(int... colors){
        DataListLayout.Bridge bridge=mBridge;
        if (null!=bridge){
            bridge.setColorSchemeColors(colors);
            return true;
        }
        return false;
    }

    public final void bindBridge(DataListLayout.Bridge bridge){
            mBridge=bridge;
            onBridgeBoundChange(null!=bridge);
    }

    public final boolean setProgressBackgroundColorSchemeColor(@ColorInt int color){
        DataListLayout.Bridge bridge=mBridge;
        if (null!=bridge){
            bridge.setProgressBackgroundColorSchemeColor(color);
            return true;
        }
        return false;
    }

    public final RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public final RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

}
