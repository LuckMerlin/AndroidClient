package com.merlin.server;

import com.alibaba.fastjson.JSONObject;

public final class Response {
    private final static String LABEL_NOTE="note";
    private final static String LABEL_WHAT="what";
    private final static String LABEL_SUCCEED="succeed";
    /**
     * succeed : true
     * what : -2000
     * note : Test response data 1575529618.61553
     */

    private final boolean mSucceed;
    private int mWhat;
    private String mNote;

    private Response(boolean succeed,int what, String note){
        mSucceed=succeed;
        mWhat=what;
        mNote=note;
    }


    public static Response buildFromJson(Object json){
        if (null!=json){
            if (json instanceof JSONObject){
                JSONObject jsonObj=(JSONObject)json;
                Response data=new Response(jsonObj.getBoolean(LABEL_SUCCEED),
                        jsonObj.getIntValue(LABEL_WHAT),
                        jsonObj.getString(LABEL_NOTE)
                        );
                return data;
            }
        }
        return null;
    }

    public boolean isSucceed() {
        return mSucceed;
    }

    public int getWhat() {
        return mWhat;
    }

    public String getNote() {
        return mNote;
    }

    @Override
    public String toString() {
        return "Data{" +
                "succeed=" + mSucceed +
                ", what=" + mWhat +
                ", note='" + mNote + '\'' +
                '}';
    }
}
