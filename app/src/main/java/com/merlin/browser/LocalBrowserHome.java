package com.merlin.browser;

import android.content.Context;
import android.content.SharedPreferences;

import com.merlin.api.Label;

public class LocalBrowserHome {
    private final String homeShName="localRoot";

    public boolean set(Context context,String value){
        SharedPreferences sh=null!=context?context.getSharedPreferences(homeShName,Context.MODE_PRIVATE):null;
        SharedPreferences.Editor editor=null!=sh?sh.edit():null;
        return null!=editor&&(value==null||value.length()<=0?editor.remove(Label.LABEL_ROOT).commit():
                editor.putString(Label.LABEL_ROOT,value).commit());
    }

    public String get(Context context,String def){
        SharedPreferences sh=null!=context?context.getSharedPreferences(homeShName,Context.MODE_PRIVATE):null;
        return null!=sh?sh.getString(Label.LABEL_ROOT,def):def;
    }
}
