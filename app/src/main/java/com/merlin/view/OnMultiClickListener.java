package com.merlin.view;

import android.view.View;

public interface OnMultiClickListener<T> {
    boolean onMultiClick(View view,int clickCount, int resId, T data);
}
