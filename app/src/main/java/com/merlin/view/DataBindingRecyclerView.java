package com.merlin.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;

public class DataBindingRecyclerView extends RecyclerView {

    public DataBindingRecyclerView(@NonNull Context context) {
        super(context);
    }

    public DataBindingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DataBindingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @BindingAdapter(value = { "imageUrl" }, requireAll = true)
    public static void binding(RecyclerView view, String url) {
        Debug.D(BaseModel.class,"你不  "+view+" "+url);
    }

}
