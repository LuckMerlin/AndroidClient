package com.luckmerlin.databinding;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @deprecated
 */
public class CustomArgBinding implements BindingObject {
    private Object mArg;

    public final CustomArgBinding setArg(Object arg){
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
        //Do nothing
        return false;
    }

    public static CustomArgBinding arg(Object arg){
        return arg(arg,true);
    }

    public static CustomArgBinding arg(Object arg,boolean lifeWeak){
        return new CustomArgBinding().setArg(lifeWeak?new LifeObjectPackager().pack(lifeWeak,arg):arg);
    }
}
