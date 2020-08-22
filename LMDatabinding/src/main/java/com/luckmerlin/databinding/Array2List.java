package com.luckmerlin.databinding;

import java.util.ArrayList;
import java.util.List;

public final class Array2List {

    public List<Object> toList(List<Object> pre,Object ...objects){
        int length=null!=objects?objects.length:-1;
        if (length>0){
            pre=null!=pre?pre:new ArrayList<>(length);
            for (Object object:objects) {
                if (null!=object&&pre.add(object)){
                    //Do nothing
                }
            }
        }
        return pre;
    }
}
