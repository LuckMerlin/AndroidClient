package com.luckmerlin.core.debug;

import com.luckmerlin.core.proguard.PublishMethods;

public class Debug implements PublishMethods {
        private final static Debugger mDebugger=new Debugger();
        private Debug(){

        }

        public static void D(String msg) {
            mDebugger.D(msg);
        }

        public static void D(String tag, String msg) {
            mDebugger.D(tag,msg);
        }

        public static void W(String msg) {
            mDebugger.W(msg);
        }

        public static void W(String tag, String msg) {
            mDebugger.W(tag,msg);
        }

        public static void I(String msg) {
            mDebugger.I(msg);
        }

        public static void I(String tag, String msg) {
            mDebugger.I(tag,msg);
        }

        public static void E(String msg) {
            mDebugger.E(msg);
        }

        public static void E(String tag, String msg) {
            mDebugger.E(tag,msg);
        }

        public static void E(String msg,Throwable throwable) {
            mDebugger.E(msg,throwable);
        }

        public static void E(String tag, String msg,Throwable throwable) {
            mDebugger.E(tag,msg,throwable);
        }

}
