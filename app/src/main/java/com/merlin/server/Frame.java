package com.merlin.server;

import com.merlin.debug.Debug;
import com.merlin.oksocket.HeadReader;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class Frame {
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
    private Response response;
    private byte[] mBodySrc;
    private final byte mCode;
    private final String mEncoding;
    private final static String LABEL_FRAME_TYPE="frameType";
    private final static String LABEL_TIMESTAMP="timestamp";
    private final static String LABEL_SECRET_KEY="secretKey";
    private final static String LABEL_VERSION="version";
    private final static String LABEL_DATA="data";
    private final static String LABEL_FRAME_TEXT_MESSAGE="textMessageFrame";
    private final static String LABEL_FRAME_TEXT_DATA="textDataFrame";
    private final static String LABEL_FRAME_BYTE_DATA="byteDataFrame";

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

            String headJson=Frame.decodeText(headBytes,encoding,null);
            if (null==headJson || headJson.length()<=0){
                Debug.E(Frame.class,"Can't build frame head data.Head is EMPTY.encoding="+encoding);
                return null;
            }
            JsonReader reader=new JsonReader(headJson);
            Frame frame=new Frame(header.getCode(),encoding);
            frame.frameType=reader.getString(LABEL_FRAME_TYPE,null);
            frame.timestamp=reader.getDouble(LABEL_TIMESTAMP,0);
            frame.secretKey=reader.getString(LABEL_SECRET_KEY,null);
            frame.version=reader.getString(LABEL_VERSION,null);
            frame.response=Response.buildFromJson(reader.getJsonObject(LABEL_DATA,null));
            frame.mBodySrc=bodyBytes;
            return frame;
        }
        return null;
    }

    public byte[] getBodyBytes(){
        return mBodySrc;
    }

    public String getBodyText(){
        byte[] bytes=mBodySrc;
        String type=frameType;
        if (null!=bytes&&(null==type ||!(type.equals(LABEL_FRAME_BYTE_DATA)))){
            return  Frame.decodeText(bytes,mEncoding,null);
        }
        return null;
    }

    public boolean isBodyBytes(){
        return null!=frameType&&frameType.equals(LABEL_FRAME_BYTE_DATA);
    }

    public boolean isBodyMessage(){
        return null!=frameType&&frameType.equals(LABEL_FRAME_TEXT_MESSAGE);
    }

    public boolean isBodyText(){
        return null!=frameType&&frameType.equals(LABEL_FRAME_TEXT_DATA);
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

    public static String decodeText(byte[] bytes,String encoding,String def){
        try {
            encoding = encoding==null||encoding.length()<=0?"utf-8":encoding;
            return null!=bytes&&bytes.length>0?new String(bytes,encoding):def;
        } catch (UnsupportedEncodingException e) {
            Debug.E(Frame.class,"Can't decode text data.e="+e+" encoding="+encoding,e);
            e.printStackTrace();
        }
        return def;
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
