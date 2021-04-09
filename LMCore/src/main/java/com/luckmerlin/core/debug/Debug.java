package com.luckmerlin.core.debug;

import android.util.Log;

import com.luckmerlin.core.proguard.PublishMethods;

public class Debug implements PublishMethods {
        private static String mTag="LM";
        private static boolean mEEnable=false;

        private Debug(){

        }

        public String getTag() {
                return mTag;
        }

        public static void D(String msg) {
                D(mTag, msg);
        }

        public static void D(String tag, String msg) {
                tag = null == tag ? mTag : tag;
                Log.d(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void ED(String tag,String msg,String e){
                tag = null == tag ? mTag : tag;
                msg=null!=msg?msg:"";
                Log.d(null!=tag?tag:"", msg+(mEEnable&&null!=e?" "+e:" "));
        }

        public static void ED(String msg,String e){
                ED(null,msg,e);
        }

        public static void W(String msg) {
                W(mTag, msg);
        }

        public static void W(String tag, String msg) {
                tag = null == tag ? mTag : tag;
                Log.w(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void EW(String msg,String e){
               EW(null,msg,e);
        }

        public static void EW(String tag,String msg,String e){
                tag = null == tag ? mTag : tag;
                msg=null!=msg?msg:"";
                Log.w(null!=tag?tag:"", msg+(mEEnable&&null!=e?" "+e:" "));
        }

        public static void I(String msg) {
                I(mTag, msg);
        }

        public static void I(String tag, String msg) {
                tag = null == tag ? mTag : tag;
                Log.i(null!=tag?tag:"", null!=msg?msg:"");
        }

        public static void EI(String msg,String e){
                EI(null,msg,e);
        }

        public static void EI(String tag,String msg,String e){
                tag = null == tag ? mTag : tag;
                msg=null!=msg?msg:"";
                Log.i(null!=tag?tag:"", msg+(mEEnable&&null!=e?" "+e:" "));
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

        public static void EE(String msg,String e,Throwable throwable){
                EE(null,msg,e,throwable);
        }

        public static void EE(String tag,String msg,String e,Throwable throwable){
                tag = null == tag ? mTag : tag;
                msg=null!=msg?msg:"";
                Log.e(null!=tag?tag:"", msg+(mEEnable&&null!=e?" "+e:" "),throwable);
        }

}
