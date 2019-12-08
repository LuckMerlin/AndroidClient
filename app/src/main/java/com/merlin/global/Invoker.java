package com.merlin.global;

import com.merlin.classes.Classes;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

public class Invoker {

    public <T>T invoke(Class<T> cls,final Object object){
            if (null!=object&&null!=cls){
                return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls},(proxy,method,args)->{
                        if (isAssignableFrom(object.getClass(),cls)){
                            method.invoke(object,args);
                        }
                    Field field=findFieldAssignableFrom(object.getClass(), BaseModel.class,null);
                    if (null!=field){
                        field.setAccessible(true);
                        try {
                            Object obj= field.get(object);
                            if (null!=obj&&isAssignableFrom(obj.getClass(),cls)) {
                                method.invoke(obj, args);
                            }
                        } catch (IllegalAccessException e) {
                            //Do nothing
                        }
                    }
                    return null;
                });
            }
        return null;
    }
    private final Field findFieldAssignableFrom(Class<?> cls, Class<?> target, Class<?> max){
        while (null!=cls&&null!=target){
            Field[] fields=cls.getDeclaredFields();
            if (null!=fields&&fields.length>0){
                for (Field f:fields) {
                    if (null!=f&&target.equals(f.getType())){
                        return f;
                    }
                }
            }
            cls=cls.getSuperclass();
        }
        return null;
    }

    private boolean isAssignableFrom(Class<?> cls,final Class<?> cls2){
        if (null!=cls&&null!=cls2){
            while (null!=cls&&null!=cls2){
                if (cls.equals(cls2)){
                    return true;
                }
                Class[] interfaces=cls.getInterfaces();
                if (null!=interfaces&&interfaces.length>0){
                    for (Class f:interfaces) {
                        if (null!=f&&f.equals(cls2)){
                            return true;
                        }
                    }
                }
                cls=cls.getSuperclass();
            }
            return false;
        }
        return false;
    }


}
