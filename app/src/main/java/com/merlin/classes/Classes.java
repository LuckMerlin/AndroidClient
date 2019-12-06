package com.merlin.classes;

public class Classes {

    public boolean isAssignableFrom(Class<?> cls,Class<?> cls2){
        if (null!=cls&&null!=cls2){
             cls.getSuperclass();
//            do {
//
//            }while (cls.equals())
            if (cls.equals(cls2)){
                return true;
            }
            return false;
        }
        return false;
    }
}
