package com.luckmerlin.databinding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishMethods;

import java.lang.reflect.Constructor;

public class ViewCreator implements PublishMethods {
    private final ViewCreatorImpl mCreator=new ViewCreatorImpl();

    public final View create(Context context, Object object){
        return mCreator.create(context,object);
    }

}
