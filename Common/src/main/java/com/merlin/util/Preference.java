package com.merlin.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preference {
    private final SharedPreferences mPreference;

    public Preference(Context context){
        mPreference=null!=context?context.getSharedPreferences("FileBrowser",Context.MODE_PRIVATE):null;
    }

    public boolean putString(String key,String value){
        SharedPreferences preferences=null!=key?mPreference:null;
        SharedPreferences.Editor editor=null!=preferences?preferences.edit():null;
        return null!=editor&&editor.putString(key,value).commit();
    }

    public String getString(String key,String def){
        SharedPreferences preferences=null!=key?mPreference:null;
        return null!=preferences?preferences.getString(key,def):def;
    }
}
