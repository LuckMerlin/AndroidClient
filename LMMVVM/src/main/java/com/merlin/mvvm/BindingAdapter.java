package com.merlin.mvvm;

import android.view.View;


import com.merlin.core.debug.Debug;

public final class BindingAdapter {

    @androidx.databinding.BindingAdapter("bindModel")
    public static void bindModel(View view, Object modelClass) {
        if (null!=view&&null!=modelClass){
            ModelBinder binder=new ModelBinder();
            Model model=binder.createModel(modelClass);
            if (null!=model&&binder.bindViewModel(model,view)&&model.initialRoot(view)){
                Debug.D("Succeed bind model."+modelClass+" "+view);
            }
        }
    }
}
