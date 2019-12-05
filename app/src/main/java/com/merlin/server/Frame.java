package com.merlin.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


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
    private final static String LABEL_FRAME_TYPE="frame_type";
    private final static String LABEL_TIMESTAMP="timestamp";
    private final static String LABEL_SECRET_KEY="secretKey";
    private final static String LABEL_VERSION="version";
    private final static String LABEL_DATA="data";
    private final static String LABEL_FRAME_TEXT_MESSAGE="textMessageFrame";
    private final static String LABEL_FRAME_TEXT_DATA="textDataFrame";
    private final static String LABEL_FRAME_BYTE_DATA="byteDataFrame";

    public static Frame buildFromJson(String json){
        JsonReader reader=null!=json&&json.length()>0?new JsonReader(json):null;
        if (null!=reader){
            Frame head=new Frame();
            head.frameType=reader.getString(LABEL_FRAME_TYPE,null);
//            head.timestamp=reader.getDoubleValue(LABEL_TIMESTAMP,null);
            head.secretKey=reader.getString(LABEL_SECRET_KEY,null);
            head.version=reader.getString(LABEL_VERSION,null);
//            head.response=Response.buildFromJson(reader.get(LABEL_DATA,null));
            return head;
        }
        return null;
    }

    public boolean isTextMessage(){
        return null!=frameType&&frameType==LABEL_FRAME_TEXT_MESSAGE;
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
