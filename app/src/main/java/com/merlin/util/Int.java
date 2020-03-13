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
            int int1=bytes[offset]&0xff;
            int int2=(bytes[offset+1]&0xff)<<8;
            int int3=(bytes[offset+2]&0xff)<<16;
            int int4=(bytes[offset+3]&0xff)<<24;
            return int1|int2|int3|int4;
        }
        return def;
    }


}
