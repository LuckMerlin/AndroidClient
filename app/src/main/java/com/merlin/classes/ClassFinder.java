package com.merlin.classes;

import com.merlin.debug.Debug;

import java.lang.reflect.Field;

public class ClassFinder extends Classes{
    public final static int TYPE_FIELD=1001;
    public final static int TYPE_INTERFACE=1002;
    public final static int TYPE_DDD=10003;

    public interface Callback{
        Object onClassFound(int type,Class<?> cls,String name);
    }

    public final Field findFieldAssignableFrom(Class<?> cls,Class<?> target,Class<?> max){
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

    public final Class<?> iterateAssignableFrom(Class<?> target,Callback callback){
        if (null!=target){

        }
        return null;
    }

}
