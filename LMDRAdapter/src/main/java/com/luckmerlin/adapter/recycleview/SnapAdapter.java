package com.luckmerlin.adapter.recycleview;

import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.luckmerlin.core.proguard.PublishMethods;

import java.util.Arrays;
import java.util.Collection;

public class SnapAdapter<T> extends ListAdapter<T> implements PublishMethods {
    private final PagerSnapHelper mHelper=new PagerSnapHelper();

    public SnapAdapter(T  ...values){
        this(null!=values&&values.length>0? Arrays.asList(values):null);
    }

    public SnapAdapter(Collection<T> list){
        super(list);
    }

    private boolean attachSnapHelper(RecyclerView recyclerView){
        if (null!=recyclerView){
            mHelper.attachToRecyclerView(recyclerView);
            return true;
        }
        return false;
    }

    @Override
    public void onAttachedRecyclerView(RecyclerView recyclerView) {
        attachSnapHelper(recyclerView);
    }

    public final PagerSnapHelper getSnapHelper() {
        return mHelper;
    }
}
