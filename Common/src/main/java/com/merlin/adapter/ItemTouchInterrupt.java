package com.merlin.adapter;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class ItemTouchInterrupt extends ItemTouchHelper.Callback {
    public final static int LEFT=ItemTouchHelper.LEFT;
    public final static int RIGHT=ItemTouchHelper.RIGHT;
    public final static int UP=ItemTouchHelper.UP;
    public final static int DOWN=ItemTouchHelper.DOWN;

    protected Integer onResolveMove(RecyclerView.ViewHolder holder, RecyclerView.LayoutManager manager){
        //Do nothing
        return null;
    }

    protected Integer onResolveSlide(RecyclerView.ViewHolder holder, RecyclerView.LayoutManager manager){
        //Do nothing
        return null;
    }

    protected Boolean onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder){
        //Do nothing
        return null;
    }

    @Override
    public final int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (null!=viewHolder&&null!=recyclerView){
            RecyclerView.Adapter adapter=recyclerView.getAdapter();
            if (null!=adapter){
                RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
                Integer moveFlag=onResolveMove(viewHolder,manager);
                moveFlag=null!=moveFlag?moveFlag:adapter instanceof ItemMoveEnable?((ItemMoveEnable)adapter).onResolveMove(viewHolder,manager):null;
                Integer swipeFlag=onResolveSlide(viewHolder,manager);
                swipeFlag=null!=swipeFlag?swipeFlag:adapter instanceof ItemSlideEnable ?((ItemSlideEnable)adapter).onResolveSlide(viewHolder,manager):null;
                if (null!=moveFlag||null!=swipeFlag){
                    return makeMovementFlags(null!=moveFlag?moveFlag:0, null!=swipeFlag?swipeFlag:0);//第一个参数拖动，第二个删除侧滑
                }
            }
        }
        return 0;
    }

    @Override
    public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        RecyclerView.Adapter adapter=null!=recyclerView?recyclerView.getAdapter():null;
        if (null!=adapter&&null!=viewHolder&&adapter instanceof ListAdapter){
                final List datas=((ListAdapter)adapter).getData();
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
    public final void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        View view=null!=viewHolder?viewHolder.itemView:null;
        ViewParent parent =null!=view?view.getParent():null;
        RecyclerView.Adapter adapter=null!=parent&&parent instanceof RecyclerView?((RecyclerView)parent).getAdapter():null;
        if (null!=adapter&&adapter instanceof ListAdapter){
            int position = viewHolder.getAdapterPosition();
            Object data=((ListAdapter)adapter).getItemData(position);
            if (null!=data) {
                Boolean removed=onItemSlideRemove(position,data,direction,viewHolder);
                removed=null!=removed?removed:adapter instanceof OnItemSlideRemove?((OnItemSlideRemove)adapter).
                        onItemSlideRemove(position,data,direction,viewHolder,(needRemove)->{
                           if (needRemove){
                               ((ListAdapter) adapter).removeData(data, "After slide remover callback.");
                           }
                        }):null;
                if (null!=removed&&removed){
                    ((ListAdapter) adapter).removeData(data, "After slide remover call.");
                }else{
                    adapter.notifyItemChanged(position);
                }
            }
        }
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
