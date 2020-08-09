package com.merlin.core.debug;

import android.util.Log;

public class Debug {
        private static String mTag="LM";

        public static void D(String msg) {
            D(mTag, msg);
        }

        public static void D(String tag, String msg) {
            tag = null == tag ? mTag : tag;
            Log.d(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void W(String msg) {
            W(mTag, msg);
        }

        public static void W(String tag, String msg) {
            tag = null == tag ? mTag : tag;
            Log.w(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void I(String msg) {
            I(mTag, msg);
        }

        public static void I(String tag, String msg) {
            tag = null == tag ? mTag : tag;
            Log.i(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void E(String msg) {
            E(mTag, msg);
        }

        public static void E(String tag, String msg) {
            Log.e(tag, msg,null);
        }

        public static void E(String msg,Throwable throwable) {
                E(null,msg,throwable);
        }

        public static void E(String tag, String msg,Throwable throwable) {
            tag = null == tag ? mTag : tag;
            Log.e(null!=tag?tag:"", null!=msg?msg:"",throwable);
        }


}
