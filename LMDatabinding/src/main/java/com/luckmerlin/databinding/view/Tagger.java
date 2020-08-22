package com.luckmerlin.databinding.view;

import android.view.View;

import com.luckmerlin.databinding.BindingObject;
import com.luckmerlin.databinding.ViewAttachRegister;

import java.util.Map;
import java.util.WeakHashMap;

final class Tagger implements BindingObject {
    private static Map<View,Object> mMaps;

    public static boolean put(View view,Object tag){
        if (null!=view){
            Map<View,Object> maps=mMaps;
            if (null!=tag){
                maps=null!=maps?maps:(mMaps=new WeakHashMap<>());
                maps.put(view,tag);
                final Map<View,Object> finalMaps=maps;
                return new ViewAttachRegister().register(view,()->{
                    finalMaps.remove(view);
                    checkEmpty();
                });
            }else{
                maps.remove(view);
                checkEmpty();
                return true;
            }
        }
        checkEmpty();
        return false;
    }

    public static Object get(View view){
        Map<View,Object> maps=null!=view?mMaps:null;
        Object tag=null!=maps?maps.get(view):null;
        checkEmpty();
        return tag;
    }

    static boolean checkEmpty(){
        Map<View,Object> maps=mMaps;
        if (null!=maps&&maps.size()<=0){
            mMaps=null;
            return true;
        }
        return false;
    }

}
