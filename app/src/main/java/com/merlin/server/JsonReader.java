package com.merlin.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public final class JsonReader {
    private final JSONObject mJson;

    public JsonReader(String json){
        Object object=null!=json&&json.length()>0? JSON.parse(json):null;
        mJson =null!=object&&object instanceof JSONObject?(JSONObject)object:null;
    }

    public String getString(String key,String def){
        Object object=null!=key&&null!=mJson?mJson.get(key):null;
        if (null!=key){

        }
        return def;
    }

}
