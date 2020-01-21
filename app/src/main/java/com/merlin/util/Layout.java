package com.merlin.util;

import androidx.recyclerview.widget.LinearLayoutManager;

public final class Layout {
    public final static int LINEAR_LAYOUT = LinearLayoutManager.HORIZONTAL;
    public final static int LINEAR_VERTICAL = LinearLayoutManager.VERTICAL;
    public final static int LINEAR_GIRD = LinearLayoutManager.VERTICAL+1;
    private final boolean mReverseLayout;
    private final int mSpanCount;
    private final int mOrientation;

    private Layout(int orientation, boolean reverseLayout, int spanCount){
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

    public static Layout layout(int orientation, boolean reverseLayout){
        return layout(orientation,reverseLayout,-1);
    }

    public static Layout layout(int orientation, boolean reverseLayout,int spanCount){
        return new Layout(orientation,reverseLayout,spanCount);
    }

}
