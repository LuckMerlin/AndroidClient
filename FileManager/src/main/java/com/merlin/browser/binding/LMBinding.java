package com.merlin.browser.binding;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class LMBinding {
    
    @BindingAdapter("src")
    public static void setSrc(ImageView view, Object path) {
        new PathGlider().glide(view,path,false);
    }

}
