package com.merlin.util;

import androidx.recyclerview.widget.RecyclerView;

public final class Layout {
    public final static int LINEAR_LAYOUT = 111;
    public final static int GRID_LAYOUT = 112;
    public final static int STAGGERED_GRID_LAYOUT = 113;
    public final static int VERTICAL = RecyclerView.VERTICAL;
    public final static int HORIZONTAL = RecyclerView.HORIZONTAL;
    private final boolean mReverseLayout;
    private final int mLayout;
    private final int mSpanCount;
    private final int mOrientation;

    private Layout(int layout,int orientation, boolean reverseLayout, int spanCount){
        mLayout=layout;
        mReverseLayout=reverseLayout;
        mOrientation=orientation;
        mSpanCount=spanCount;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public boolean isReverseLayout() {
        return mReverseLayout;
    }

    public int getSpanCount() {
        return mSpanCount;
    }

    public int getLayout() {
        return mLayout;
    }

    public static Layout layout(int layout,int orientation, boolean reverseLayout){
        return layout(layout,orientation,reverseLayout,-1);
    }

    public static Layout layout(int layout,int orientation, boolean reverseLayout,int spanCount){
        return new Layout(layout,orientation,reverseLayout,spanCount);
    }

}
