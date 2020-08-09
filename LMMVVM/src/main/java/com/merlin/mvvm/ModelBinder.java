package com.merlin.mvvm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.core.debug.Debug;
import com.merlin.mvvm.activity.ModelResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class ModelBinder {

    protected final Class<? extends Model> findModelClass(Class cls){
        while (true){
            if (null==cls){
                break;
            }
            if (cls.equals(Model.class)){
                return cls;
            }
            cls=cls.getSuperclass();
        }
        return null;
    }

    protected final Model createDeclaredModel(Object object){
        Object model=null!=object&&object instanceof ModelResolver ?((ModelResolver)object).onResolveModel():null;
        if (null!=model){
            if (model instanceof Class){
                Constructor[] constructors=null!=findModelClass((Class)model)?((Class) model).getDeclaredConstructors():null;
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
                            model=constructor.newInstance();
                        } catch (Exception e) {
                            //Do nothing
                        }
                    }
                }
            }
            return null!=model&&model instanceof Model?((Model)model):null;
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
            Class cls=null!=binding?binding.getClass():null;
            Method[] methods=null!=cls?cls.getDeclaredMethods():null;
            Class modelClass=model.getClass();
            if (null!=methods&&methods.length>0){
                for (Method method:methods) {
                    Type[] types=null!=method?method.getParameterTypes():null;
                    Type type=null!=types&&types.length==1?types[0]:null;
                    if (null!=type&&type.equals(modelClass)){
                        try {
                            Debug.D("Bind view model");
                            method.invoke(model);
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

}
