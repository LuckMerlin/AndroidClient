package com.luckmerlin.core.debug;

import com.luckmerlin.core.proguard.PublishMethods;

public class Debug implements PublishMethods {
        public static void D(String msg) {
            Debugger.D(msg);
        }

        public static void D(String tag, String msg) {
            Debugger.D(tag,msg);
        }

        public static void W(String msg) {
            Debugger.W(msg);
        }

        public static void W(String tag, String msg) {
            Debugger.W(tag,msg);
        }

        public static void I(String msg) {
            Debugger.I(msg);
        }

        public static void I(String tag, String msg) {
            Debugger.I(tag,msg);
        }

        public static void E(String msg) {
            Debugger.E(msg);
        }

        public static void E(String tag, String msg) {
            Debugger.E(tag,msg);
        }

        public static void E(String msg,Throwable throwable) {
            Debugger.E(msg,throwable);
        }

        public static void E(String tag, String msg,Throwable throwable) {
            Debugger.E(tag,msg,throwable);
        }

}
