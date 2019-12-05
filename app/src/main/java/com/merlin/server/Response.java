package com.merlin.server;

import com.alibaba.fastjson.JSONObject;

public class Response {
    private final static String LABEL_NOTE="note";
    private final static String LABEL_WHAT="what";
    private final static String LABEL_SUCCEED="succeed";
    /**
     * succeed : true
     * what : -2000
     * note : Test response data 1575529618.61553
     */

    private boolean succeed;
    private int what;
    private String note;

    public static Response buildFromJson(Object json){
        if (null!=json){
            if (json instanceof JSONObject){
                Response data=new Response();
                JSONObject jsonObj=(JSONObject)json;
                data.note=jsonObj.getString(LABEL_NOTE);
                data.what=jsonObj.getIntValue(LABEL_WHAT);
                data.succeed=jsonObj.getBoolean(LABEL_SUCCEED);
                return data;
            }
        }
        return null;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public int getWhat() {
        return what;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "Data{" +
                "succeed=" + succeed +
                ", what=" + what +
                ", note='" + note + '\'' +
                '}';
    }
}
