package com.merlin.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
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
//
//    @BindingConversion
//    public static ColorDrawable convertColorToDrawable(int color) {
//        return new ColorDrawable(color);
//    }

}
