package com.luckmerlin.core.util;

import com.luckmerlin.core.proguard.PublishMethods;

import java.io.Closeable;
import java.io.IOException;

public final class Closer implements PublishMethods {

    public int close(Closeable ...closeables) {
        return close(true,false,closeables);
    }

    public int close(boolean safe,boolean print,Closeable ...closeables) {
        if (null!=closeables&&closeables.length>0){
            int closed=0;
            for (Closeable child:closeables) {
                if (null!=child){
                    try {
                        child.close();
                        closed+=1;
                    } catch (IOException e) {
                        if (print){
                            e.printStackTrace();
                        }
                        if (!safe){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return closed;
        }
        return 0;
    }
}
