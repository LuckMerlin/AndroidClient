package com.merlin.adapter;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewScrollQuantize extends RecyclerView.OnScrollListener {
    private volatile Integer mTop;

    @Override
    public final void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Integer top = mTop;
        if (null != top) {
            View view=recyclerView.findChildViewUnder(0,0);
            RecyclerView.ViewHolder holder=null!=view?recyclerView.getChildViewHolder(view):null;

//            recyclerView
//            (()recyclerView.getLayoutManager())
//            recyclerView.getLayoutManager().get

//            ItemListFileBindingImpl dd;
//            Debug.D(getClass(),"AAAAAAa sdfas "+dx+" "+dy+" "+ (null!=holder&&holder instanceof ListAdapter.ViewHolder?
//                    ((ListAdapter.ViewHolder)holder).getItemId():null)
//            );
        }
//        recyclerView.findChildViewUnder();}
    }

    @Override
    public final void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState){
            case RecyclerView.SCROLL_STATE_DRAGGING://get Through
            case RecyclerView.SCROLL_STATE_SETTLING:
                RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
                if (null!=manager&&manager instanceof LinearLayoutManager){
                    LinearLayoutManager llm=(LinearLayoutManager)manager;
                    llm.findFirstVisibleItemPosition();
//                    recyclerView.findContainingItemView()
                }
                mTop=null!=mTop?mTop:recyclerView.getTop();
                break;
            case RecyclerView.SCROLL_STATE_IDLE:
                mTop=null;
                break;
        }
//        mTop=recyclerView.getTop();
//        Debug.D(getClass(),"AAAA "+newState+" ");
//        public static final int SCROLL_STATE_IDLE = 0;
//
//        /**

//         * The RecyclerView is currently being dragged by outside input such as user touch input.
//         * @see #getScrollState()
//         */
//        public static final int SCROLL_STATE_DRAGGING = 1;
//
//        /**
//         * The RecyclerView is currently animating to a final position while not under
//         * outside control.
//         * @see #getScrollState()
//         */
//        public static final int SCROLL_STATE_SETTLING = 2;


    }
}
