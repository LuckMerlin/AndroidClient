package com.merlin.debug;

import android.util.Log;

public class Debug {
        private static String mTag;
        private static boolean mPrintClass = false;
        private static final String DEFAULT_TAG = "LM";

        public Debug() {
        }

        public static void D(Class<?> cls, String msg) {
            D(cls, mTag, msg);
        }

        public static void D(Class<?> cls, String tag, String msg) {
            tag = null == tag ? mTag : tag;
            if (isNeedPrintDebug(3, cls, tag, msg)) {
                Log.d(null == tag ? DEFAULT_TAG : tag, formatMessage(cls, msg));
            }

        }

        public static void I(Class<?> cls, String msg) {
            I(cls, mTag, msg);
        }

        public static void I(Class<?> cls, String tag, String msg) {
            tag = null == tag ? mTag : tag;
            if (isNeedPrintDebug(4, cls, tag, msg)) {
                Log.i(null == tag ? DEFAULT_TAG : tag, formatMessage(cls, msg));
            }

        }

        public static void W(Class<?> cls, String msg) {
            W(cls, mTag, msg);
        }

        public static void W(Class<?> cls, String tag, String msg) {
            tag = null == tag ? mTag : tag;
            if (isNeedPrintDebug(5, cls, tag, msg)) {
                Log.w(null == tag ? DEFAULT_TAG : tag, formatMessage(cls, msg));
            }

        }

        public static void E(Class<?> cls, String msg) {
            E(cls, mTag, msg, (Exception)null);
        }

        public static void E(Class<?> cls, String msg, Exception e) {
            E(cls, mTag, msg, e);
        }

        public static void E(Class<?> cls, String tag, String msg, Exception e) {
            tag = null == tag ? mTag : tag;
            if (isNeedPrintDebug(6, cls, tag, msg)) {
                Log.e(null == tag ? DEFAULT_TAG : tag, formatMessage(cls, msg), e);
            }

        }

        private static String formatMessage(Class<?> cls, String msg) {
            return (null == msg ? "" : msg) + (mPrintClass ? (null == cls ? "" : cls) : "");
        }


        private static boolean isNeedPrintDebug(int type, Class<?> cls, String tag, String msg) {
            return true;
        }

    }
