package com.merlin.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.debug.Debug;


public class MenuTextView extends androidx.appcompat.widget.AppCompatTextView {


    public MenuTextView(@NonNull Context context) {
        this(context, null);
    }

    public MenuTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Drawable[] drawables=getCompoundDrawables();
        Drawable drawable=null!=drawables&&4==drawables.length?drawables[1]:null;
        if (null!=drawable){
            drawable.setBounds(0,0,100,100);
            setCompoundDrawables(null,drawable,null,null);
        }
    }



}