package com.luckmerlin.databinding;
import android.view.View;

import com.luckmerlin.core.proguard.PublishFields;

/**
 * @deprecated
 */
public final class CreatedModel implements PublishFields {
    public final MatchBinding mMatchBinding;
    public final Model mModel;
    public final View mRoot;

    protected CreatedModel(Model model, View root, MatchBinding matchBinding){
        mModel=model;
        mRoot=root;
        mMatchBinding=matchBinding;
    }

}
