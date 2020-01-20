package com.merlin.util;

import android.content.Context;

public class Text {

    public static String text(Context context,String def, Integer id){
        try {
            String text=null!=id&&null!=context?context.getResources().getString(id):null;
            return null!=text?text:def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }
}
