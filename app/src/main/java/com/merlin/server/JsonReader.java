package com.merlin.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public final class JsonReader {
    private final JSONObject mJson;

    public JsonReader(String json){
        Object object=null!=json&&json.length()>0? JSON.parse(json):null;
        mJson =null!=object&&object instanceof JSONObject?(JSONObject)object:null;
    }

    public String getString(String key,String def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof String?(String)object:def;
    }

    public double getDouble(String key,double def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof Double?(Double)object:def;
    }

    public long getLong(String key,long def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof Long?(Long) object:def;
    }

    public float getFloat(String key,float def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof Float?(Float) object:def;
    }

    public JSONObject getJsonObject(String key,JSONObject def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof JSONObject?(JSONObject) object:def;
    }

    public JSONArray getJsonArray(String key,JSONArray def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        return null!=object&&object instanceof JSONArray?(JSONArray) object:def;
    }





}
