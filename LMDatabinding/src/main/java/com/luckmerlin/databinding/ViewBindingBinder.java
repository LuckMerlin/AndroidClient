package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public final class ViewBindingBinder implements PublishMethods {
    private final ViewBindingBinderImpl mImpl=new ViewBindingBinderImpl();

    public boolean bind(View view, BindingObject ...bindings){
        return mImpl.bind(view,bindings);
    }
}
