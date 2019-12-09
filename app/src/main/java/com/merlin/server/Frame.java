package com.merlin.server;

import com.merlin.protocol.Protocol;
import com.merlin.protocol.Tag;
import com.merlin.debug.Debug;
import com.merlin.oksocket.Head;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class Frame implements Tag {
    /**
     * msg_type : message
     * timestamp : 1.5755296186155417E9
     * secretKey :
     * version : 0.0.1
     * data : {"succeed":true,"what":-2000,"note":"Test response data 1575529618.61553"}
     */

    private final String mFrameType;
    private final double mTimestamp;
    private final String mSecretKey;
    private final String mVersion;
    private final String mUnique;
    private final Response mResponse;
    private final byte[] mBodySrc;
    private final byte mCode;
    private final String mEncoding;
    private final byte[] mMsgFromBytes;
    private final byte[] mMsgToBytes;

    public Frame(byte code,String encoding,byte[] msgFromBytes,byte[] msgToBytes,String frameType,
               double timestamp,String secretKey,String unique,String version,Response response,byte[] bodyBytes){
        mMsgFromBytes=msgFromBytes;
        mMsgToBytes=msgToBytes;
        mFrameType=frameType;
        mTimestamp=timestamp;
        mSecretKey=secretKey;
        mVersion=version;
        mUnique=unique;
        mResponse=response;
        mBodySrc=bodyBytes;
        mCode=code;
        mEncoding=encoding;

    }

    public byte[] getBodyBytes(){
        return mBodySrc;
    }

    public String getUnique() {
        return mUnique;
    }

    public String getBodyText(){
        byte[] bytes=mBodySrc;
        String type=mFrameType;
        if (null!=bytes&&(null==type ||!(type.equals(TAG_FRAME_BYTE_DATA)))){
            return  Frame.decodeString(bytes,mEncoding,null);
        }
        return null;
    }

    public String getMsgFrom(){
        byte[] bytes=mMsgFromBytes;
        return null!=bytes&&bytes.length>0?decodeString(bytes,mEncoding,null):null;
    }

    public String getMsgTo(){
        byte[] bytes=mMsgToBytes;
        return null!=bytes&&bytes.length>0?decodeString(bytes,mEncoding,null):null;
    }


    public byte[] getMsgFromBytes() {
        return mMsgFromBytes;
    }

    public byte[] getMsgToBytes() {
        return mMsgToBytes;
    }

    public boolean isFrameType(String frameType){
        String curr=mFrameType;
        return null!=frameType&&null!=curr&&frameType.equals(curr);
    }

    public String getFrameType() {
        return mFrameType;
    }

    public double getTimestamp() {
        return mTimestamp;
    }

    public String getSecretKey() {
        return mSecretKey;
    }

    public String getVersion() {
        return mVersion;
    }

    public Response getResponse() {
        return mResponse;
    }

    public String getEncoding(){
        return mEncoding;
    }

    public byte getCode(){
        return mCode;
    }

    public static String decodeString(byte[] bytes,String encoding,String def){
        try {
            encoding = encoding==null||encoding.length()<=0?"utf-8":encoding;
            return null!=bytes&&bytes.length>0?new String(bytes,encoding):def;
        } catch (UnsupportedEncodingException e) {
            Debug.E(Frame.class,"Can't decode text data.e="+e+" encoding="+encoding,e);
            e.printStackTrace();
        }
        return def;
    }

    public static byte[] encodeString(String string){
            return encodeString(string, Protocol.ENCODING);
    }

    public static byte[] encodeString(String string,String encoding){
        if (null!=string&&string.length()>0){
            try {
                return string.getBytes(null!=encoding&&encoding.length()>0?encoding:"utf-8");
            } catch (UnsupportedEncodingException e) {
                Debug.E(Frame.class,"Can't encode string.e="+e+" encoding="+encoding,e);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "mFrameType='" + mFrameType + '\'' +
                ", mTimestamp=" + mTimestamp +
                ", mSecretKey='" + mSecretKey + '\'' +
                ", mVersion='" + mVersion + '\'' +
                ", mUnique='" + mUnique + '\'' +
                ", mResponse=" + mResponse +
                ", mBodySrc=" + Arrays.toString(mBodySrc) +
                ", mCode=" + mCode +
                ", mEncoding='" + mEncoding + '\'' +
                ", mMsgFromBytes=" + Arrays.toString(mMsgFromBytes) +
                ", mMsgToBytes=" + Arrays.toString(mMsgToBytes) +
                '}';
    }
}
