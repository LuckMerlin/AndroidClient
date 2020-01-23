package com.merlin.util;

import androidx.annotation.IdRes;

import java.lang.reflect.Field;

public class Resource {


    public static boolean isExistResource(Class<?> cls,@IdRes Integer id){
        Field[] fields=null!=cls&&null!=id?cls.getDeclaredFields():null;
        if (null!=fields&&fields.length>0){
            for (Field field:fields) {
                if (null!=field){
                    try {
                        if (!field.isAccessible()){
                            field.setAccessible(true);
                        }
                        Object object= field.get(null);
                        if (null!=object&& object instanceof Integer&&(int) object==id){
                            return true;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
