package com.luckmerlin.databinding;

import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.match.Matchable;

public class ModelClassFinder implements PublishMethods {
    private final ModelClassFinderImpl mImpl=new ModelClassFinderImpl();

    public final MatchBinding find(ViewDataBinding binding, Matchable matchable){
        return find(binding,matchable,null);
    }

    public final MatchBinding findModel(ViewDataBinding binding, Matchable matchable){
        return find(binding,matchable, LModel.class);
    }

    public final MatchBinding find(ViewDataBinding binding, Matchable matchable, Class cls){
        return mImpl.find(binding,matchable,cls);
    }

    public final MatchBinding findModel(View modelRoot, Matchable matchable){
        if (null!=modelRoot&&DataBindingUtil.checkDataBindingEnable(false)){
            ViewDataBinding binding=DataBindingUtil.getBinding(modelRoot);
            return null!=binding?findModel(binding,matchable):null;
        }
        return null;
    }

    public final MatchBinding findModel(Class modelClass, Matchable matchable){
        return mImpl.find(modelClass,matchable,LModel.class);
    }

    public final boolean isExistClass(Class srcClass,final String clsTargetName){
        return mImpl.isExistClass(srcClass,clsTargetName);
    }

}
