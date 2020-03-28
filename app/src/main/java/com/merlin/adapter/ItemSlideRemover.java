package com.merlin.adapter;

import android.view.View;
import android.view.ViewParent;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

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
        if (null!=adapter&&adapter instanceof ListAdapter){
            int position = viewHolder.getAdapterPosition();
            Object data=((ListAdapter)adapter).getItemData(position);
            if (null!=data) {
                ((ListAdapter) adapter).removeData(data, "After slide remover call.");
                WeakReference<OnItemSlideRemove> reference=mSlideRemove;
                OnItemSlideRemove remove=null!=reference?reference.get():null;
                if (null!=remove){
                    remove.onItemSlideRemoved(position,data);
                }
            }
        }
    }

}
