package com.merlin.util;

public class Int {

    public static byte[] toByteArray(int i,byte[] result,int offset) {
        if (null==result||offset+4>=result.length){
            result=new byte[4];offset=0;
        }
        result[0+offset] = (byte)((i >> 24) & 0xFF);
        result[1+offset] = (byte)((i >> 16) & 0xFF);
        result[2+offset] = (byte)((i >> 8) & 0xFF);
        result[3+offset] = (byte)(i & 0xFF);
        return result;
    }

    public static Integer toInt(byte[] bytes,int offset,Integer def) {
        int length=null!=bytes?bytes.length:0;
        if (length>0&&offset>=0&&offset<length-1){
            int result = bytes[offset+0] & 0xFF;
            result = (result << 8) | (bytes[offset+1] & 0xff) ;
            result = (result << 8) | (bytes[offset+2] & 0xff) ;
            result = (result << 8) | (bytes[offset+3] & 0xff) ;
            return result;
        }
        return def;
    }


}
