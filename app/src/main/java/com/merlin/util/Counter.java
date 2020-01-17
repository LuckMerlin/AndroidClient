package com.merlin.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class Counter {

    public static int size(Object obj){
        if (null!=obj){
            if (obj instanceof Collection){
                return ((Collection)obj).size();
            }else  if (obj instanceof Map){
                return ((Map)obj).size();
            }else  if (obj.getClass().isArray()){
                return Array.getLength(obj);
            }
        }
        return -1;
    }
}
