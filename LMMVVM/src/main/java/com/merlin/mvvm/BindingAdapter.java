package com.merlin.mvvm;

import android.view.View;

import androidx.databinding.ViewDataBinding;


public final class BindingAdapter {

    @androidx.databinding.BindingAdapter("bindModel")
    public static void bindModel(View view, Object modelObj) {
        if (null!=view&&null!=modelObj){
            final ModelBinder binder=new ModelBinder();
            if (modelObj instanceof Boolean){
                if (!(Boolean)modelObj){
                    return;//Not need bind model
                }
                ViewDataBinding binding=DataBindingUtil.getBinding(view);
                modelObj=null!=binding?binder.findBindingModelClass(binding):null;
            }
            Model model=null!=modelObj?binder.createModel(modelObj):null;
            if (null!=model&&binder.attachModel(view,model)){
                //Succeed
            }
        }
    }
}
