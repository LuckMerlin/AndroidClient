package com.luckmerlin.core.debug;

import android.os.Bundle;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.Set;

public final class Dump implements PublishMethods {

    public StringBuffer dump(Bundle bundle){
        Set<String> set=null!=bundle?bundle.keySet():null;
        int size=null!=set?set.size():0;
        if (size>0){
            StringBuffer buffer=new StringBuffer();
            for (String child:set) {
                buffer.append(" "+child+"="+bundle.get(child));
            }
            return buffer;
        }
        return null;
    }
}
