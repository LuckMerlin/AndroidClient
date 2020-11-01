package com.luckmerlin.databinding;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

 final class Resources {

    public String getString(android.content.res.Resources resources, int textId, String def,Object ... args){
        try {
            return null!=resources?resources.getString(textId,args):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

    public CharSequence getText(android.content.res.Resources resources, int textId, CharSequence def){
        try {
            return null!=resources?resources.getText(textId,def):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

    public Integer getColorFromText(String colorText, Integer def){
        try {
            return null!=colorText&&colorText.length()>0? Color.parseColor(colorText):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

     public ColorStateList getColor(String pkgName,android.content.res.Resources resources, String colorResName, ColorStateList def){
         return null!=colorResName&&null!=pkgName&&null!=resources?getColor(resources,resources.getIdentifier(colorResName,"color",pkgName),def):def;
     }

    public ColorStateList getColor(android.content.res.Resources resources, int colorId, ColorStateList def){
        try {
            return null!=resources?Build.VERSION.SDK_INT >= Build.VERSION_CODES.M?
                    resources.getColorStateList(colorId,null):resources.getColorStateList(colorId):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }

    public Drawable getColorDrawable(android.content.res.Resources resources, int colorId, Drawable def){
        ColorStateList color=null!=resources?getColor(resources,colorId,null):null;
        return null!=color?new ColorDrawable(color.getDefaultColor()):def;
    }

     public Drawable getDrawable(String pkgName,android.content.res.Resources resources, String drawableName, Drawable def){
        return null!=drawableName&&null!=pkgName&&null!=resources?getDrawable(resources,resources.getIdentifier(drawableName,"drawable",pkgName),def):def;
    }

    public Drawable getDrawable(android.content.res.Resources resources, int drawableId, Drawable def){
        try {
            return null!=resources&&drawableId!=-1&&drawableId!=0?Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP?
                    resources.getDrawable(drawableId,null):resources.getDrawable(drawableId):def;
        }catch (Exception e){
            //Do nothing
        }
        return def;
    }
}
