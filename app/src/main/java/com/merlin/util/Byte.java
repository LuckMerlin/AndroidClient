package com.merlin.util;

public class Byte {

    public static String dump(byte[] bytes){
        StringBuffer buffer=new StringBuffer();
        int length=null!=bytes?bytes.length:0;
        if (length>0){
            for (int i = 0; i < length; i++) {
                 buffer.append(" "+Integer.toHexString(bytes[i]));
            }
        }
        return buffer.toString();
    }
}
