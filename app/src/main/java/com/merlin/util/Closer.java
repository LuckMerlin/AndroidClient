package com.merlin.util;
import java.io.Closeable;
import java.io.IOException;

public final class Closer {

    public boolean close(Closeable... closeables){
        if (null!=closeables&&closeables.length>0) {
            for (Closeable child : closeables) {
                try {
                    if (null!=child) {
                        child.close();
                    }
                } catch (IOException e) {
                    //Do nothing
                }
            }
            return true;
        }
        return false;
    }
}
