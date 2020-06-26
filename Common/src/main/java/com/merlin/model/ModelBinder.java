package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.merlin.lib.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ModelBinder {

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

    public static Model getBindModel(View view){
        Object object=null!=view?view.getTag(R.id.modelBind):null;
        return null!=object&&object instanceof Model?(Model)object:null;
    }

    public boolean bind(View view,Object  modeClassName){
        if (null!=view){
            Class perferClass=findModelClass(view,modeClassName);
            ViewDataBinding binding=null!=view?DataBindingUtil.bind(view):null;
            Class cls=null!=binding?binding.getClass().getSuperclass():null;
            Field[] fields=null!=cls?cls.getDeclaredFields():null;
            if (null!=fields&&fields.length>0){
                for (Field field:fields) {
                    Class type=null!=field?field.getType():null;
                    if (null!=type&& Classes.isAssignableFrom(type,Model.class)&&(null==perferClass||
                            perferClass.equals(type))){
                        Model model=createModel(view,type);
                        if (null!=model){
                            try {
                            field.setAccessible(true);
                            String fieldName=field.getName();
                            fieldName=null!=fieldName&&fieldName.startsWith("m")?fieldName.replaceFirst("m",""):fieldName;
                            fieldName="set"+fieldName;
                            Method[] methods=null!=fieldName?cls.getDeclaredMethods():null;
                            if (null!=methods&&methods.length>0){
                                String methodName;
                                for (Method method:methods) {
                                    methodName=null!=method?method.getName():null;
                                    if (null!=methodName&&methodName.equals(fieldName)){
                                        method.setAccessible(true);
                                        method.invoke(binding,model);
                                    }
                                }
                            }
                                view.setTag(R.id.modelBind,model);
                                Context context=view.getContext();
                                if (null!=context){
                                    if (context instanceof ModelActivity){
                                        ((ModelActivity)context).onModelBind(model);
                                    }
                                    if (model instanceof Model.OnActivityIntentChange){
                                        Intent intent=null!=context&&context instanceof Activity?((Activity)context).getIntent():null;
                                        if (null!=intent){
                                            ((Model.OnActivityIntentChange)model).onActivityIntentChanged((Activity)context,intent);
                                        }
                                    }
                                }
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
