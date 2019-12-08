package com.merlin.protocol;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Head;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.server.Response;

import java.nio.ByteOrder;
import java.util.Arrays;

public final class Protocol {
   public final static String ENCODING="utf-8";
   public final static ByteOrder BYTE_ORDER=ByteOrder.BIG_ENDIAN;
   public final static String PROTOCOL_VERSION="0.0.1";
    public final static int HEAD_LENGTH =22; //1+1+5+5+5+5

   public static Head readHead(byte[] bytes, boolean bigOrder){
// #   1         1         5           5               5        5       msgFrom     msgTo       head     content
//#  code    encoding  headSize  contentSize    msgToSize msgFromSize msgFromData  msgToData  headData  contentData
       if (null!=bytes&&bytes.length>=HEAD_LENGTH) {
           byte code =bytes[0];
           byte encoding=bytes[1];
           Long headLength = Protocol.bytes2Long(bytes, 0, bigOrder, 2, 7);
           Long contentLength = Protocol.bytes2Long(bytes, 0, bigOrder, 7, 12);
           Long msgToLength = Protocol.bytes2Long(bytes,0,bigOrder,12,17);
           Long msgFromLength = Protocol.bytes2Long(bytes,0,bigOrder,17,22);
           if (null==headLength||null==contentLength){
               Debug.W(Head.class,"Can't build head,Length is invalid."+headLength+" "+contentLength);
               return null;
           }
           int head=(int)((long) headLength);
           int content=(int)((long) contentLength);
           int msgFrom=(int)((long)msgFromLength);
           int msgTo =(int)((long)msgToLength);
           return new Head(code,encoding,(head+content+msgFrom+msgTo),msgFrom,msgTo,head,content);
       }
       return null;
   }


    public static Frame buildFromBytes(byte[] headBytes,byte[] bodyBytes){
// #   1         1         5           5               5        5       msgFrom     msgTo       head     content
//#  code    encoding  headSize  contentSize    msgToSize msgFromSize msgFromData  msgToData  headData  contentData

        Head head=null!=headBytes&&headBytes.length>0? Protocol.readHead(headBytes,true):null;
        if (null!=head){
            String encoding=head.getEncodingName();
            encoding=null!=encoding&&encoding.length()>0?encoding:"utf-8";
            int msgFromSize=head.getMsgFromSize();
            int msgToSize=head.getMsgToSize();
            int headSize=head.getHeadSize();
            int bodySize=head.getContentSize();
            int total=msgFromSize+msgToSize+headSize+bodySize;
            int size=null!=bodyBytes?bodyBytes.length:-1;
            if(size<total){
                return null; //Invalid
            }
            int end=0;
            byte[] msgFromDataBytes= msgFromSize > 0 ? Arrays.copyOfRange(bodyBytes,0, end=msgFromSize) : null;
            byte[] msgToDataBytes = msgToSize > 0 ? Arrays.copyOfRange(bodyBytes, end, end+=msgToSize) : null;
            byte[] headDataBytes= headSize >0 ? Arrays.copyOfRange(bodyBytes,end,end+=headSize) : null;
            byte[] bodyDataBytes= bodySize >0 ? Arrays.copyOfRange(bodyBytes,end,end+bodySize):null;
            String headJson=null!=headDataBytes&&headDataBytes.length>0?Frame.decodeString(headDataBytes,encoding,null):null;
            if (null==headJson || headJson.length()<=0){
                Debug.E(Frame.class,"Can't build frame head data.Head is EMPTY.encoding="+encoding);
                return null;
            }
            Json reader=new Json(headJson);
            Frame frame=new Frame(head.getCode(),encoding,
                    msgFromDataBytes,msgToDataBytes,
                    reader.getString(Tag.TAG_FRAME_TYPE,null),
                    reader.getDouble(Tag.TAG_TIMESTAMP,0),
                    reader.getString(Tag.TAG_SECRET_KEY,null),
                    reader.getString(Tag.TAG_UNIQUE,null),
                    reader.getString(Tag.TAG_VERSION,null),
                    Response.buildFromJson(reader.getJsonObject(Tag.TAG_DATA,null)),
                    bodyDataBytes
                    );
            return frame;
        }
        return null;
    }

   public static byte[] generateFrame(String msgTo,byte[] head,byte[] data){
        return generateFrame(msgTo,head,data,ENCODING);
    }

    public static byte[] generateFrame(String msg_to,byte[] head,byte[] data,String decoding){
// #   1         1         5           5               5        5       msgFrom     msgTo       head     content
//#  code    encoding  headSize  contentSize    msgToSize msgFromSize msgFromData  msgToData  headData  contentData
        String msg_from=null;
        byte[] msgFromBytes =null!=msg_from&&msg_from.length()>0?Frame.encodeString(msg_from):null;
        byte[] msgToBytes= null!=msg_to &&msg_to.length()>0? Frame.encodeString(msg_to):null;
        int msgToBytesLength = null!=msgToBytes?msgToBytes.length:0;
        int msgFromBytesLength =null!=msgFromBytes?msgFromBytes.length:0;
        int headLength=null!=head?head.length:0;
        int dataLength=null!=data?data.length:0;
        byte codeByte=(byte)0;
        byte encoding=(byte)(null!=decoding&&decoding.equalsIgnoreCase("utf-8")?1:0);
        //
        byte[] headLengthBytes=integer2ByteArray(headLength, 5);
        byte[] dataLengthBytes=integer2ByteArray(dataLength, 5);
        byte[] msgToLengthBytes=integer2ByteArray(msgToBytesLength, 5);
        byte[] msgFromLengthBytes=integer2ByteArray(0,5);
        //
        byte[] result=new byte[22+msgFromBytesLength+msgToBytesLength+headLength+dataLength];
        //
        result[0]=codeByte;
        result[1]=encoding;
        System.arraycopy(headLengthBytes,0,result,2,5);
        System.arraycopy(dataLengthBytes,0,result,7,5);
        System.arraycopy(msgToLengthBytes,0,result,12,5);
        System.arraycopy(msgFromLengthBytes,0,result,17,5);
        int start=22;
        if (msgFromBytesLength>0){
            System.arraycopy(msgFromBytes,0,result,start,msgFromBytesLength);
            start+=msgFromBytesLength;
        }
       // Debug.D(Protocol.class,"WWWWW "+start+" "+end +" "+msgToBytesLength+" "+result.length);
        if (msgToBytesLength>0){
            System.arraycopy(msgToBytes,0,result,start,msgToBytesLength);
            start+=msgToBytesLength;
        }
//        Debug.D(Protocol.class,"QQQQQ "+start+" "+end +" "+headLength+" "+result.length)
        if (headLength>0){
            System.arraycopy(head,0,result, start,headLength);
            start+=headLength;
        }
        if (dataLength>0){
            System.arraycopy(data,0,result,start,dataLength);
//            start+=dataLength;
        }
        return result;
    }

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
