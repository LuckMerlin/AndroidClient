package com.luckmerlin.databinding;

import android.content.Context;

import com.luckmerlin.core.proguard.PublishMethods;

public class ModelBinder implements PublishMethods {
    private final ModelBinderImpl mImpl=new ModelBinderImpl();

    public MatchBinding bindModelForObject(Context context,Object object, String debug){
        return mImpl.bindModelForObject(context,object,debug);
    }

}
