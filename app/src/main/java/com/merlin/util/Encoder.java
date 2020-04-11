package com.merlin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Encoder {

    public final String encode(String name, String def,String encoding){
        try {
            return null!=name&&name.length()>0? URLEncoder.encode(name,null!=encoding&&encoding.
                    length()>0?encoding: "UTF-8"):def;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return def;
    }
}
