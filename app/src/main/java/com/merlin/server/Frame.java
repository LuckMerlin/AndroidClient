package com.merlin.server;

import com.merlin.global.Protocol;
import com.merlin.global.Tag;
import com.merlin.debug.Debug;
import com.merlin.oksocket.HeadReader;

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

    private String frameType;
    private double timestamp;
    private String secretKey;
    private String version;
    private String unique;
    private Response response;
    private byte[] mBodySrc;
    private final byte mCode;
    private final String mEncoding;

    private Frame(byte code,String encoding){
        mCode=code;
        mEncoding=encoding;
    }

    public static Frame buildFromBytes(byte[] head,byte[] body){
        HeadReader header=null!=head? HeadReader.read(head,true):null;
        if (null!=head){
            String encoding=header.getEncodingName();
            encoding=null!=encoding&&encoding.length()>0?encoding:"utf-8";
            int headSize=header.getHeadSize();
            int contentSize=header.getContentSize();
            int total=headSize+contentSize;
            int size=null!=body?body.length:-1;
            if(size<total){
                return null; //Invalid
            }
            byte[] headBytes= headSize >0 && headSize<= size? Arrays.copyOfRange(body,0,headSize):null;
            byte[] bodyBytes= headSize >0 && total< size?Arrays.copyOfRange(body,headSize,total):null;

            String headJson=Frame.decodeString(headBytes,encoding,null);
            if (null==headJson || headJson.length()<=0){
                Debug.E(Frame.class,"Can't build frame head data.Head is EMPTY.encoding="+encoding);
                return null;
            }
            JsonReader reader=new JsonReader(headJson);
            Frame frame=new Frame(header.getCode(),encoding);
            frame.frameType=reader.getString(TAG_FRAME_TYPE,null);
            frame.timestamp=reader.getDouble(TAG_TIMESTAMP,0);
            frame.secretKey=reader.getString(TAG_SECRET_KEY,null);
            frame.unique=reader.getString(TAG_UNIQUE,null);
            frame.version=reader.getString(TAG_VERSION,null);
            frame.response=Response.buildFromJson(reader.getJsonObject(TAG_DATA,null));
            frame.mBodySrc=bodyBytes;
            return frame;
        }
        return null;
    }

    public byte[] getBodyBytes(){
        return mBodySrc;
    }

    public String getUnique() {
        return unique;
    }

    public String getBodyText(){
        byte[] bytes=mBodySrc;
        String type=frameType;
        if (null!=bytes&&(null==type ||!(type.equals(TAG_FRAME_BYTE_DATA)))){
            return  Frame.decodeString(bytes,mEncoding,null);
        }
        return null;
    }

    public boolean isBodyBytes(){
        return null!=frameType&&frameType.equals(TAG_FRAME_BYTE_DATA);
    }

    public boolean isBodyMessage(){
        return null!=frameType&&frameType.equals(TAG_FRAME_TEXT_MESSAGE);
    }

    public boolean isBodyText(){
        return null!=frameType&&frameType.equals(TAG_FRAME_TEXT_DATA);
    }

    public String getFrameType() {
        return frameType;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getVersion() {
        return version;
    }

    public Response getResponse() {
        return response;
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
                "frameType='" + frameType + '\'' +
                ", timestamp=" + timestamp +
                ", secretKey='" + secretKey + '\'' +
                ", version='" + version + '\'' +
                ", response=" + response +
                '}';
    }
}
