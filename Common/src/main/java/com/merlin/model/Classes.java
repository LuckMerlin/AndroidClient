package com.merlin.model;


import android.content.Context;

import java.lang.reflect.Constructor;

public final class Classes {

    public static boolean isAssignableFrom(Class from,Class to) {
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

//    public static boolean isAssignableFrom(Class<?> cls,final Class<?> cls2){
//        if (null!=cls&&null!=cls2){
//             while (null!=cls&&null!=cls2){
//                    if (cls.equals(cls2)){
//                        return true;
//                    }
//                    cls=cls.getSuperclass();
//             }
//            return false;
//        }
//        return false;
//    }

    public static Object instance(String classValue, Context context){
        try {
            Class cls=null!=classValue?Class.forName(classValue):null;
            Constructor[] constructors=null!=cls?cls.getConstructors():null;
            if (null!=constructors&&constructors.length>0){
                for (Constructor constructor:constructors) {
                    if (null!=constructor){
                        Class[] classes=constructor.getParameterTypes();
                        int length=null!=classes?classes.length:-1;
                        if (length>0){
                            if (length==1){
                                Class type=classes[0];
                                if (null!=context&&null!=type&&type.equals(Context.class)){
                                    return constructor.newInstance(context);
                                }
                            }
                        }else{
                            return constructor.newInstance();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
