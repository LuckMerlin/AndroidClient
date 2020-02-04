package com.merlin.binding;

import android.content.Context;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.model.Model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModelBinder {

    private boolean isAssignableFrom(Class from,Class to) {
        if (null!=from&&null!=to){
            Class cls=from;
            do {
                if (cls.equals(to)){
                    return true;
                }
            }while(null!=(cls=(cls.getSuperclass()))&&(cls!=from));
        }
        return false;
    }

    private Model createModel(View view,Class modelClass){
       if (null!=view&&null!=modelClass) {
           Constructor[] constructors= modelClass.getDeclaredConstructors();
           if ((null!=constructors?constructors.length:-1)>0){
               try {
                   Object object=null;
                   Class type;
                   for (Constructor constructor:constructors) {
                       if (null==constructor){
                           continue;
                       }
                       constructor.setAccessible(true);
                       Class[] types=constructor.getParameterTypes();
                       int length=null!=types?types.length:-1;
                       if (length==0){
                           object=constructor.newInstance();
                       }else if (length==1&&null!=(type=types[0])){
                           if (type.isAssignableFrom(Context.class)){
                               object=constructor.newInstance(view.getContext().getApplicationContext());
                           }else if (type.isAssignableFrom(View.class)){
                               object=constructor.newInstance(view);
                           }
                       }
                       if (null!=object&&object instanceof Model){
                           Method method= Model.class.getDeclaredMethod("initial",View.class);
                           method.setAccessible(true);
                           method.invoke(object,view);
                           return (Model) object;
                       }
                    }
               }catch (Exception e){
                   //Do nothing
               }
           }
            return null;
        }
        return null;
    }

    public boolean bind(View view,Object  modeClassName){
        if (null!=view){
            Class perferClass=findModelClass(view,modeClassName);
            ViewDataBinding binding=null!=view?DataBindingUtil.getBinding(view):null;
            Class cls=null!=binding?binding.getClass().getSuperclass():null;
            Field[] fields=null!=cls?cls.getDeclaredFields():null;
            if (null!=fields&&fields.length>0){
                for (Field field:fields) {
                    Class type=null!=field?field.getType():null;
                    if (null!=type&&isAssignableFrom(type,Model.class)&&(null==perferClass||
                            perferClass.equals(type))){
                        Model model=createModel(view,type);
                        if (null!=model){
                            field.setAccessible(true);
                            try {
                                field.set(binding,model);
                                DataBindingUtil.bind(view);
                                view.setTag(R.id.modelBind,model);
                                binding.invalidateAll();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }
        }
        return false;
    }

    private Class findModelClass(View view,Object object){
        if (null!=view&&null!=object){
            if (object instanceof String){
                if (!((String)object).contains(".")){
                    object= Model.class.getPackage().getName()+"."+object;
                }
                try {
                    return null!=object&&object instanceof String?Class.forName((String)object):null;
                } catch (ClassNotFoundException e) {
                   //Do nothing
                }
            }
        }
        return null;
    }

}
