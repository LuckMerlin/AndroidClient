package com.merlin.util;
import java.io.Closeable;
import java.io.IOException;

public final class Closer {

    public boolean close(Closeable closeable){
        if (null!=closeable){
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
               //Do nothing
            }
        }
        return false;
    }
}
