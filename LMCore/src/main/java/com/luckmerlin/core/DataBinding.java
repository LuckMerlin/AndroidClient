package com.luckmerlin.core;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;

public final class DataBinding implements PublishMethods {


    public final boolean checkDataBindingEnable(boolean print){
        try {
            Class.forName("androidx.databinding.DataBinderMapperImpl");
            return true;
        }catch (ClassNotFoundException e) {
            if (print) {
                Debug.W("Maybe you need configure databinding enable in build.gradle for module as follow.\n\nandroid {\n  dataBinding{\n     enabled true\n  }\n}\n\n");
            }
        }
        return false;
    }

}
