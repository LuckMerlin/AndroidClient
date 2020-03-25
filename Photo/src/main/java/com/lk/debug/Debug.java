package com.lk.debug;

import android.util.Log;

public class Debug {

    public static void D(String log){
         D(null,log);
    }

    public static void D(String tag,String log){
        tag=null!=tag?tag:"LM";
        Log.d(tag,null!=log?log:"");
    }
}
