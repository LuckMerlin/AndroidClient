package com.merlin.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.merlin.debug.Debug;
import com.merlin.oksocket.Socket;

import org.json.JSONException;

public final class Json {
    private final JSONObject mJson;

    public Json(String json){
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

    public static final boolean putIfNotNull(org.json.JSONObject json, String key, Object value){
        if (null!=json&&null!=key&&null!=value){
            try {
                json.put(key,value);
            } catch (JSONException e) {
                Debug.E(Socket.class,"Can't put string into json.e="+e+" key="+key+" value="+value,e);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static final boolean putIfNotNull(JSONObject json, String key, Object value){
        if (null!=json&&null!=key&&null!=value){
            json.put(key,value);
            return true;
        }
        return false;
    }


}
