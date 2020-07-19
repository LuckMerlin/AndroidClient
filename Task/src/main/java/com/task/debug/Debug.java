package com.task.debug;

import android.util.Log;

public class Debug {

    public static void D(String msg){
        Log.d("LM",null!=msg?msg:"");
    }

    public static void E(String msg){
        E(msg,null);
    }

    public static void E(String msg,Throwable throwable){
        Log.e("LM",null!=msg?msg:"");
    }

    public static void W(String msg){
        Log.w("LM",null!=msg?msg:"");
    }
}
