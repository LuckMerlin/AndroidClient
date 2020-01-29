package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.adapter.BaseAdapter;
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            private boolean mScrolling=false;

            private boolean isNeedLoadMore(RecyclerView recyclerView){
                RecyclerView.LayoutManager manager=null!=recyclerView?recyclerView.getLayoutManager():null;
                if (null==manager){
                    return false;
                }
                int currentCount=0;
                int tailPosition=0;
                if (manager instanceof LinearLayoutManager){
                    currentCount=manager.getItemCount();
                    tailPosition=((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                }
                if ((currentCount<=0|| tailPosition == currentCount - 1)) {
                    return true;
                }
                return false;
            }

            private void loadMore(RecyclerView recyclerView,String debug){
                RecyclerView.Adapter adapter= null!=recyclerView?recyclerView.getAdapter():null;
                BaseAdapter.OnLoadMore loadMore=null!=adapter&&adapter instanceof BaseAdapter?((BaseAdapter)adapter).getOnLoadMore():null;
                if (null!=loadMore&&loadMore.onLoadMore()){

                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mScrolling=newState==RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING;
                if (newState==RecyclerView.SCROLL_STATE_IDLE&&isNeedLoadMore(recyclerView)){
                    loadMore(recyclerView,"After scroll idle.");
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mScrolling&&isNeedLoadMore(recyclerView)) {
                    loadMore(recyclerView, "After scroll arrived tail.");
                }
        }
        });
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
