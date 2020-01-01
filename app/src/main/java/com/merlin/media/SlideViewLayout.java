package com.merlin.media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SlideViewLayout extends ViewPager {

    public SlideViewLayout(@NonNull Context context) {
        this(context,null);
    }

    public SlideViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        setAdapter(mAdapter);
    }


    private final PagerAdapter mAdapter=new PagerAdapter() {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return false;
        }
    };

}
