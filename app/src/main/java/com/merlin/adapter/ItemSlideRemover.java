package com.merlin.adapter;

import android.view.View;
import android.view.ViewParent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.debug.Debug;

import java.util.Collections;
import java.util.List;

public  class ItemSlideRemover extends ItemSlideMover {

    @Override
    public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        View view=null!=viewHolder?viewHolder.itemView:null;
        ViewParent parent =null!=view?view.getParent():null;
        RecyclerView.Adapter adapter=null!=parent&&parent instanceof RecyclerView?((RecyclerView)parent).getAdapter():null;
        if (null!=adapter&&adapter instanceof Adapter){
            int position = viewHolder.getAdapterPosition();
            ((Adapter)adapter).removeAt(position,"After slide remover call.");
        }
    }


}
