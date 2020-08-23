package com.luckmerlin.mvvm;

import android.view.View;

import com.luckmerlin.databinding.CustomArgBinding;

public final class ModelBinding extends CustomArgBinding {

    public static ModelBinding arg(Object arg){
        ModelBinding binding=new ModelBinding();
        binding.setArg(arg);
        return binding;
    }

    @Override
    public final void onBind(View view) {
        Object modelObj=null!=view?getArg():null;
        if (null!=modelObj){
            final ModelBinder binder=new ModelBinder();
            binder.bindFromAdapter(view,modelObj,"While binding adapter called.");
        }
    }
}
