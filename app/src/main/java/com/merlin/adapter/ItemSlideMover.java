package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class ItemSlideMover extends ItemTouchHelper.Callback {

    @Override
    public final int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (null!=viewHolder&&null!=recyclerView){
            int swiped = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            int dragFlags = 0;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            }else {
                dragFlags= ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            //第一个参数拖动，第二个删除侧滑
            return makeMovementFlags(dragFlags, swiped);
        }
        return 0;
    }

    @Override
    public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        RecyclerView.Adapter adapter=null!=recyclerView?recyclerView.getAdapter():null;
        if (null!=adapter&&null!=viewHolder&&adapter instanceof Adapter){
                final List datas=((Adapter)adapter).getData();
                int oldPosition = viewHolder.getAdapterPosition();
                int newPosition = target.getAdapterPosition();
                if (oldPosition < newPosition) {
                    for (int i = oldPosition; i < newPosition; i++) {
                        Collections.swap(datas, i, i +1);    // 改变实际的数据集
                    }
                } else {
                    for (int i = oldPosition; i > newPosition; i--) {
                        Collections.swap(datas, i, i - 1); // 改变实际的数据集
                    }
                }
                adapter.notifyItemMoved(oldPosition, newPosition);
                return true;
        }
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //Do nothing
    }

    //    @Override
//    public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        View view=null!=viewHolder?viewHolder.itemView:null;
//        if (null!=view){
//            Debug.D(getClass(),"SS onSwiped SSSSSS "+view+" "+view.getParent());
////            int position = viewHolder.getAdapterPosition();
////            RecyclerView.Adapter adapter=recyclerView.getAdapter();
////            mdatas.remove(position);
////            myAdapter.notifyItemRemoved(position);
//        }
//    }
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        View view=viewHolder.itemView;
//        if (null!=view&&actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
//            view.setBackgroundColor(Color.parseColor("#303F9F"));
//        }
//    }
//    @Override
//    public final void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        View view=viewHolder.itemView;
//        if (null!=view) {
//            view.setBackgroundColor(Color.TRANSPARENT);
//        }
//    }


}
