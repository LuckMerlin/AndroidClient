package com.luckmerlin.databinding;

import android.content.Context;

import com.luckmerlin.core.proguard.PublishMethods;

public final class ModelCreator implements PublishMethods {
    private final ModelCreatorImpl mImpl=new ModelCreatorImpl();

    public final CreatedModel createModel(Context context, Object object,String debug) {
        return mImpl.createModel(context,object,debug);
    }
}
