package com.merlin.binding;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import java.lang.reflect.Constructor;

public final class Adapter {
    private final String mAdapterClass;

    private Adapter(String adapterClass){
        mAdapterClass=adapterClass;
    }

    public static Adapter adapter(String adapterClass){
        return new Adapter(adapterClass);
    }

    public boolean inflate(RecyclerView rv){
        String cls=mAdapterClass;
        Object instance=null!=cls&&null!=rv?instance(cls,rv.getContext()):null;
        if (null!=instance&&instance instanceof BaseAdapter){
            RecyclerView.LayoutManager lm=instance instanceof BaseAdapter.OnLayoutManagerResolve ?
                    ((BaseAdapter.OnLayoutManagerResolve)instance).onResolveLayoutManager(rv):null;
            if (null!=lm){
                rv.setLayoutManager(lm);
            }
            if (instance instanceof BaseAdapter.OnAdapterBind){
                ((BaseAdapter.OnAdapterBind)instance).onAdapterBind(rv);
            }
            rv.setAdapter((BaseAdapter)instance);
            return true;
        }
        return false;
    }

    public String getAdapterClass() {
        return mAdapterClass;
    }

    private Object instance(String classValue, Context context){
        try {
            Class cls=null!=classValue?Class.forName(classValue):null;
            Constructor[] constructors=null!=cls?cls.getConstructors():null;
            if (null!=constructors&&constructors.length>0){
                for (Constructor constructor:constructors) {
                    if (null!=constructor){
                        Class[] classes=constructor.getParameterTypes();
                        int length=null!=classes?classes.length:-1;
                        if (length>0){
                            if (length==1){
                                Class type=classes[0];
                                if (null!=context&&null!=type&&type.equals(Context.class)){
                                    return constructor.newInstance(context);
                                }
                            }
                        }else{
                            return constructor.newInstance();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
