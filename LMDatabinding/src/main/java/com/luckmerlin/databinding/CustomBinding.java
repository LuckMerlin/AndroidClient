package com.luckmerlin.databinding;

import android.view.View;

import java.lang.ref.WeakReference;

public class CustomBinding implements BindingObject{
    private Object mArg;

    public final CustomBinding setArg(Object arg){
        mArg=arg;
        return this;
    }

    public final Object getArg(){
        Object arg=mArg;
        if (null!=arg&&arg instanceof WeakReference){
            if (null==(arg=((WeakReference)arg).get())){
                mArg=null;
            }
        }
        return arg;
    }

    @Override
    public boolean onBind(View view) {
        return false;
    }

    public static CustomBinding arg(Object arg){
        return arg(arg,true);
    }

    public static CustomBinding arg(Object arg,boolean lifeWeak){
        return new CustomBinding().setArg(new LifeObjectPackager().pack(lifeWeak,arg));
    }
}
