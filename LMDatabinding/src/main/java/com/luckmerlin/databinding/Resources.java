package com.luckmerlin.databinding;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.luckmerlin.core.proguard.PublishMethods;

public final class Resources {

    public CharSequence getText(android.content.res.Resources resources, int textId, CharSequence def){
        return null!=resources?resources.getText(textId,def):null;
    }

    public Integer getTextColor(String colorText, Integer def){
        try {
            return null!=colorText&&colorText.length()>0? Color.parseColor(colorText):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

    public int getColor(android.content.res.Resources resources, int colorId, int def){
        try {
            return null!=resources?Build.VERSION.SDK_INT >= Build.VERSION_CODES.M?
                    resources.getColor(colorId,null):resources.getColor(colorId):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

    public Drawable getColorDrawable(android.content.res.Resources resources, int colorId, Drawable def){
        Integer color=null!=resources?getColor(resources,colorId,0):null;
        return null!=color&&color!=0?new ColorDrawable(color):def;
    }


    public Drawable getDrawable(android.content.res.Resources resources, int drawableId, Drawable def){
        try {
            return null!=resources?Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP?
                    resources.getDrawable(drawableId,null):resources.getDrawable(drawableId):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }
}
