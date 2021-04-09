package com.luckmerlin.databinding;

import android.view.View;

/**
 * @deprecated
 */
public final class LayoutInflateBinding implements IBinding {

    public boolean inflateLayout(View view, Object layout){
        return new LayoutInflateBindingImpl().inflateLayout(view,layout);
    }
}
