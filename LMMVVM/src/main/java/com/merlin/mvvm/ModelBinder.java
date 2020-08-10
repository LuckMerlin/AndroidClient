package com.merlin.mvvm;

import android.content.Context;
import android.graphics.ColorSpace;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.core.debug.Debug;
import com.merlin.mvvm.activity.ModelResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class ModelBinder {

    protected final Class<? extends Model> getModelClass(Object object){
        if (null!=object){
            if (object instanceof String){
                try {
                    object=Class.forName((String)object);
                } catch (ClassNotFoundException e) {
                    //Do nothing
                }
            }
            Class cls=null!=object&&object instanceof Class?(Class)object:null;
            while (true){
                if (null==cls){
                    break;
                }
                if (cls.equals(Model.class)){
                    return (Class)object;
                }
                cls=cls.getSuperclass();
            }
        }
        return null;
    }

    protected final Model createDeclaredModel(Object object) {
        Object model = null != object && object instanceof ModelResolver ? ((ModelResolver) object).onResolveModel() : null;
        return null!=model?createModel(model):null;
    }

    protected final Model createModel(Object object){
        if (null!=object){
            object=object instanceof String?getModelClass((String)object):object;
            if (object instanceof Class){
                Constructor[] constructors=null!=getModelClass((Class)object)?((Class) object).getDeclaredConstructors():null;
                if (null!=constructors&&constructors.length>0){
                    Constructor constructor=null;
                    for (Constructor child:constructors) {
                        Class[] types=null!=child?child.getParameterTypes():null;
                        if (null!=types&&types.length==0){
                            constructor=child;
                            break;
                        }
                    }
                    if (null!=constructor){//Found model constructor,Now create ir
                        try {
                            object=constructor.newInstance();
                        } catch (Exception e) {
                            //Do nothing
                        }
                    }
                }
            }
            return null!=object&&object instanceof Model?((Model)object):null;
        }
        return null;
    }

    protected final View createModelView(Context context, Object object) {
        if (null!=object&&null!=context){
            if (object instanceof Integer){//Found as layout id
                try {
                    ViewDataBinding binding=DataBindingUtil.inflate(LayoutInflater.from(context), (Integer)object,null,false);
                    object=null!=binding?binding.getRoot():object;
                }catch (Exception e){
                    //Do nothing
                }
                try {
                    object=null!=object&&object instanceof Integer?View.inflate(context,(Integer)object,null):object;
                }catch (Exception e){
                    //Do nothing
                }
            }
            return null!=object&&object instanceof View&&((View)object).getParent()==null?(View)object:null;
        }
        return null;
    }

    protected final boolean bindViewModel(Model model,View view){
        if (null!=model&&null!=view){
            ViewDataBinding binding=DataBindingUtil.getBinding(view);
            Debug.D("AAAAAAAAAAAa "+binding);
            Class cls=null!=binding?binding.getClass():null;
            Method[] methods=null!=cls?cls.getDeclaredMethods():null;
            Class modelClass=model.getClass();
            if (null!=methods&&methods.length>0){
                for (Method method:methods) {
                    Type[] types=null!=method?method.getParameterTypes():null;
                    Type type=null!=types&&types.length==1?types[0]:null;
                    Debug.D("ASDSFADA  "+method.getName()
                    );
                    if (null!=type&&type.equals(modelClass)){
                        try {
                            Debug.D("Succeed bind view model."+method.getName()+" "+type+" "+modelClass);
                            method.invoke(model);
                            Debug.D("Succeed bind view model."+view);
                            return true;
                        } catch (Exception e) {
                            Debug.D("Succeed bind view model."+e);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

}
