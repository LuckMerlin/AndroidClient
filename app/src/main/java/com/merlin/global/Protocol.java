package com.merlin.global;

import com.merlin.debug.Debug;

import java.nio.ByteOrder;

public final class Protocol {
   public final static String ENCODING="utf-8";
   public final static ByteOrder BYTE_ORDER=ByteOrder.BIG_ENDIAN;
   public final static String PROTOCOL_VERSION="0.0.1";

    public static byte[] generateFrame(byte[] head,byte[] data){
        return generateFrame(head,data,ENCODING);
    }

    public static byte[] generateFrame(byte[] head,byte[] data,String decoding){
        int headLength=null!=head?head.length:0;
        int dataLength=null!=data?data.length:0;
        byte codeByte=(byte)0;
        byte encoding=(byte)(null!=decoding&&decoding.equalsIgnoreCase("utf-8")?1:0);
        byte[] headLengthBytes=integer2ByteArray(headLength, 5);
        byte[] dataLengthBytes=integer2ByteArray(dataLength, 5);
        byte[] result=new byte[12+headLength+dataLength];//1+1+5+headBytes+5+dataBytes
        result[0]=codeByte;
        result[1]=encoding;
        System.arraycopy(headLengthBytes,0,result,2,5);
        System.arraycopy(dataLengthBytes,0,result,7,5);
        if (headLength>0){
            System.arraycopy(head,0,result,12,headLength);
        }
        if (dataLength>0){
            System.arraycopy(data,0,result,12+headLength,dataLength);
        }
//
//        String ddd="Generate ";
//        for (byte f:result ) {
//            ddd+=" "+ Integer.toHexString(f & 0xFF);
//        }
//        Debug.D(getClass(),ddd);
//        head
//        byte[] dataBytes=integer2ByteArray(dataLength, 5);
//        data
//        head_size = 0 if head_bytes is None else len(head_bytes)
//        if head_size is None or head_size > max_length:
//        debug_e("Can't generate head bytes.head size generate failed.", head_size, head_bytes, encoding)
//        return None
//        encoding_bytes = (1 if encoding == "utf-8" else 0).to_bytes(1, byte_order)
//        return code_bytes+encoding_bytes + head_size.to_bytes(max_length_byte, byte_order) + head_bytes + \
//        length.to_bytes(max_length_byte, byte_order)
        return result;
    }

//    public static  byte[] integer2ByteArray(long data,int length){
//        return ByteBuffer.allocate(length<=0||length>=20000?1:length).order(BYTE_ORDER).putLong(1,data).array();
//    }

    public static byte[] integer2ByteArray(long values, int length) {
        if (length>0&&length<=8) {
            byte[] buffer = new byte[length];
            int end=8-length;
            for (int i = 0; i < 8; i++) {
                if (i >= end) {
                    buffer[i - end] = (byte) ((values >> (64 - (i + 1) << 3)) & 0xff);
                }
            }
            return buffer;
        }
        Debug.W(Protocol.class,"Can't integer to byte array.length="+length);
        return null;
    }
//0 0 0 0 -72

    public static Long bytes2Long(byte[] bytes,int offset,boolean big,int start,int end) {
        int length=null!=bytes?bytes.length:-1;
        if (length<=0||(start>end)||end>length){
//            Debug.D(Protocol.class," "+start+" "+end+" "+length);
            return null;
        }
        long result=0;
        for (int i = start; i < end; i++) {
//            Debug.D(Protocol.class," i="+i+" "+(bytes[i]&0xff));
            result|=(bytes[i]&0xff)<<(8*(big?(end-i-1):(i-start)));
        }
//        Debug.D(Protocol.class,"RRRRRRRRRRR "+result);
        return result;
    }

    public static String dumpBytes(byte[] bytes,boolean print){
        String buffer=new String();
        int count=null!=bytes?bytes.length:0;
        for (int i = 0; i < count; i++) {
            buffer+=(bytes[i]+" ");
        }
        if (print){
            Debug.D(Protocol.class,buffer);
        }
        return buffer;
    }
}
