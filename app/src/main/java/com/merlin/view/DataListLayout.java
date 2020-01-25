package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.debug.Debug;
import com.merlin.model.DataListModel;


public class DataListLayout extends SwipeRefreshLayout {
    private final RecyclerView mRecyclerView;

    public interface Bridge{
        void setRefreshing(boolean refreshing);
        void setColorSchemeColors(@ColorInt int... colors);
        void setProgressBackgroundColorSchemeColor(@ColorInt int color);
    }

    public DataListLayout(@NonNull Context context) {
        this(context, null);
    }

    public DataListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        addView(mRecyclerView=new RecyclerView(context),new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        //        setOnChildScrollUpCallback(new OnChildScrollUpCallback() {
//            @Override
//            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
//                return false;
//            }
//        });
//        setProgressViewEndTarget(false,0);
//        setDistanceToTriggerSync(1);
//        setDistanceToTriggerSync(1);
//        setSlingshotDistance(23);
//        setProgressViewEndTarget(true,50);
//        dd.setNestedScrollingEnabled();
    }


    public final void setViewModel(DataListModel model){
        if (null!=model){
            setAdapter(model.getAdapter());
            setLayoutManager(model.getLayoutManager());
            model.bindBridge(mBridge);
            if (model instanceof OnRefreshListener){
                setOnRefreshListener((OnRefreshListener)model);
            }
        }
    }

    public final boolean setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView recyclerView=mRecyclerView;
        if (null!=recyclerView){
            recyclerView.setAdapter(adapter);
            return true;
        }
        return false;
    }

    public final boolean setLayoutManager(RecyclerView.LayoutManager manager) {
        RecyclerView recyclerView=mRecyclerView;
        if (null!=recyclerView){
            recyclerView.setLayoutManager(manager);
            return true;
        }
        return false;
    }

    private final Bridge mBridge=new Bridge(){
        @Override
        public void setRefreshing(boolean refreshing) {
            post(()-> DataListLayout.this.setRefreshing(refreshing));
        }

        @Override
        public void setProgressBackgroundColorSchemeColor(int color) {
            post(()->DataListLayout.this.setProgressBackgroundColorSchemeColor(color));
        }

        @Override
        public void setColorSchemeColors(int... colors) {
            post(()->DataListLayout.this.setColorSchemeColors(colors));
        }
    };
}
