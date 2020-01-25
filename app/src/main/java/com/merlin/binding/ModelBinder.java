package com.merlin.binding;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


import com.merlin.classes.Classes;
import com.merlin.model.BaseModel;

import java.lang.reflect.Field;

public class ModelBinder {

    public boolean bind(View view,String  modeClassName){
        if (null!=view){
            ViewDataBinding binding=DataBindingUtil.getBinding(view);
            Class cls=null!=binding?binding.getClass().getSuperclass():null;
            Field field=null!=cls?findClassField(cls,modeClassName):null;
            Class type=null!=field?field.getType():null;
            Object object=null!=type?Classes.instance(type.getName(),view.getContext()):null;
            if (null!=type){
                field.setAccessible(true);
                try {
                    field.set(binding,object);
                    return true;
                } catch (IllegalAccessException e) {
                   //Do nothing
                }
            }
        }
        return false;
    }

    private Field findClassField(Class cls,String modeClassName){
        Field[] fields=null!=cls?cls.getDeclaredFields():null;
        if (null!=fields&&fields.length>0){
            Class type;
            for (Field field:fields) {
                if (null!=(type=null!=field?field.getType():null)&& Classes.isAssignableFrom(type, BaseModel.class)){
                    if (null==modeClassName||Classes.isAssignableFrom(type,modeClassName)){
                        return field;
                    }
                }
            }
        }
        return null;
    }
}
