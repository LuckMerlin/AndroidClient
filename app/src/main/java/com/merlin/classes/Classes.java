package com.merlin.classes;


public class Classes {

    public boolean isAssignableFrom(Class<?> cls,final Class<?> cls2){
        if (null!=cls&&null!=cls2){
             while (null!=cls&&null!=cls2){
                    if (cls.equals(cls2)){
                        return true;
                    }
                    cls=cls.getSuperclass();
             }
            return false;
        }
        return false;
    }
}
