package com.luckmerlin.core.debug;

import android.util.Log;

/**
 * Create LuckMerlin
 * Date 14:19 2018/5/12
 * TODO
 */
final class Debugger {
    private static String mTag="LM";

    public String getTag() {
        return mTag;
    }

    public void D(String msg) {
        D(mTag, msg);
    }

    public void D(String tag, String msg) {
        tag = null == tag ? mTag : tag;
        Log.d(null!=tag?tag:"", null!=msg?msg:"");
    }

    public void W(String msg) {
        W(mTag, msg);
    }

    public  void W(String tag, String msg) {
        tag = null == tag ? mTag : tag;
        Log.w(null!=tag?tag:"", null!=msg?msg:"");
    }

    public void I(String msg) {
        I(mTag, msg);
    }

    public void I(String tag, String msg) {
        tag = null == tag ? mTag : tag;
        Log.i(null!=tag?tag:"", null!=msg?msg:"");
    }

    public void E(String msg) {
        E(mTag, msg);
    }

    public void E(String tag, String msg) {
        Log.e(tag, msg,null);
    }

    public void E(String msg,Throwable throwable) {
        E(null,msg,throwable);
    }

    public void E(String tag, String msg,Throwable throwable) {
        tag = null == tag ? mTag : tag;
        Log.e(null!=tag?tag:"", null!=msg?msg:"",throwable);
    }

}
