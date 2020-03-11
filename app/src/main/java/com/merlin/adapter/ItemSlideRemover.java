package com.merlin.adapter;

import android.view.View;
import android.view.ViewParent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class ItemSlideRemover extends ItemSlideMover {
    public interface OnItemSlideRemove{
        void onItemSlideRemoved(int position,Object data);
    }

    private WeakReference<OnItemSlideRemove> mSlideRemove;

    public boolean setOnItemSlideRemove(OnItemSlideRemove remove){
        WeakReference<OnItemSlideRemove> reference=mSlideRemove;
        mSlideRemove=null;
        if (null!=reference){
            reference.clear();
        }
        if (null!=remove){
            mSlideRemove=new WeakReference<>(remove);
        }
        return false;
    }

    @Override
    public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        View view=null!=viewHolder?viewHolder.itemView:null;
        ViewParent parent =null!=view?view.getParent():null;
        RecyclerView.Adapter adapter=null!=parent&&parent instanceof RecyclerView?((RecyclerView)parent).getAdapter():null;
        if (null!=adapter&&adapter instanceof Adapter){
            int position = viewHolder.getAdapterPosition();
            Object data=((Adapter)adapter).getItem(position);
            if (null!=data) {
                ((Adapter) adapter).remove(data, "After slide remover call.");
                WeakReference<OnItemSlideRemove> reference=mSlideRemove;
                OnItemSlideRemove remove=null!=reference?reference.get():null;
                if (null!=remove){
                    remove.onItemSlideRemoved(position,data);
                }
            }
        }
    }

}
