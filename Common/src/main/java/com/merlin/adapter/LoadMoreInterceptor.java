package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.debug.Debug;

public abstract class LoadMoreInterceptor extends RecyclerView.OnScrollListener  {
    public final static int TAIL =121;
    public final static int IDLE =122;
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

    protected abstract void onLoadMore(RecyclerView recyclerView,int state,String debug);

    protected void onRecyclerScrollStateChanged(RecyclerView recyclerView,int newState){
        //Do nothing
    }

    @Override
    public final void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        mScrolling=newState==RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING;
        if (newState==RecyclerView.SCROLL_STATE_IDLE&&isNeedLoadMore(recyclerView)){
            onLoadMore(recyclerView,IDLE,"After scroll idle.");
        }
        onRecyclerScrollStateChanged(recyclerView,newState);
    }



    @Override
    public final void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mScrolling&&isNeedLoadMore(recyclerView)) {
            onLoadMore(recyclerView, TAIL,"After scroll arrived tail.");
        }
        onRecyclerScrolled(recyclerView,dx,dy);
    }

    protected void onRecyclerScrolled(RecyclerView recyclerView,int dx, int dy){
        //Do nothing
    }


}
