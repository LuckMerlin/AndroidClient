package com.luckmerlin.databinding;
import android.view.View;

import com.luckmerlin.core.proguard.PublishFields;

public final class CreatedModel implements PublishFields {
    public final MatchBinding mMatchBinding;
    public final LModel mModel;
    public final View mRoot;

    protected CreatedModel(LModel model,View root,MatchBinding matchBinding){
        mModel=model;
        mRoot=root;
        mMatchBinding=matchBinding;
    }

}
