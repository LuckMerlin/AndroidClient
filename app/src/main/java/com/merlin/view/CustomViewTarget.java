package com.merlin.view;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.transition.Transition;
import com.merlin.debug.Debug;

public  class CustomViewTarget<T extends View, Z> extends com.bumptech.glide.request.target.CustomViewTarget<T, Z>{

    public CustomViewTarget(@NonNull T view) {
        super(view);
    }

    @Override
    protected void onResourceCleared(@Nullable Drawable placeholder) {

    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {

    }

    @Override
    public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
    }
}
