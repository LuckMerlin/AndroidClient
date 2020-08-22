package com.luckmerlin.databinding;

import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;

public final class LMBinding implements PublishMethods {

    public static boolean bind(View view, BindingObject ...bindingObjects){
        return new ViewBinding().bind(view,bindingObjects);
    }

    public static BindingObject array(BindingObject ...bindingObjects){
        return array(false,bindingObjects);
    }

    public static BindingObject array(boolean skipExist,BindingObject ...bindingObjects){
        return null!=bindingObjects&&bindingObjects.length>0?new BindingList().append(skipExist,bindingObjects):null;
    }

}
