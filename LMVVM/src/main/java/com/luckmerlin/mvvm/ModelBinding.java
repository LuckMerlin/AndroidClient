package com.luckmerlin.mvvm;

import android.view.View;

import com.luckmerlin.databinding.CustomArgBinding;
import com.luckmerlin.databinding.ViewBindingBinder;


/**
 * @deprecated
 */
final class ModelBinding extends CustomArgBinding {

    private ModelBinding(){

    }

    public static ModelBinding arg(Object arg){
        ModelBinding binding=new ModelBinding();
        binding.setArg(arg);
        return binding;
    }

    @Override
    public final boolean onBind(View view) {
        Object modelObj=null!=view?getArg():null;
        if (null!=modelObj){
//            new ViewBindingBinder().bind(view,this);
        }
        return false;
    }
}
