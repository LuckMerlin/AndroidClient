package com.luckmerlin.databinding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import java.lang.reflect.Constructor;

public final class ViewCreator {

    public View create(Context context, Object object){
        if (null!=object&&null!=context){
            if (object instanceof View){
                return (View)object;
            }else if (object instanceof Integer){
                Integer layoutId=(Integer)object;
                View view=null;
                try {
                    boolean isViewDataBindLayout=true;
                    if (isViewDataBindLayout) {
                        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                                layoutId, null, false);
                        view = null != binding ? binding.getRoot() : null;
                    }
                }catch (Exception e){
                    //Do nothing
                }
                try {
                    view=null==view?View.inflate(context,layoutId,null):view;
                }catch (Exception e){
                    //Do nothing
                }
                return view;
            }else if (object instanceof ViewDataBinding){
                return ((ViewDataBinding)object).getRoot();
            }else if (object instanceof String){
                try {
                    object=Class.forName((String)object);
                } catch (ClassNotFoundException e) {
                   //Do nothing
                }
            }
            if (object instanceof Class){
                Class cls=(Class)object;
                try {
                    Constructor constructor=cls.getDeclaredConstructor(Context.class);
                    if (null!=constructor){
                        constructor.setAccessible(true);
                        Object obj=constructor.newInstance(context);
                        return null!=obj&&obj instanceof View?(View)obj:null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

}
