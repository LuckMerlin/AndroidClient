package com.luckmerlin.databinding;

import android.view.View;

public final class LayoutInflateBinding implements BindingObject{

    public boolean inflateLayout(View view, Object layout){
        return new LayoutInflateBindingImpl().inflateLayout(view,layout);
    }
}
