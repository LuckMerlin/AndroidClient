package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.view.View;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.core.proguard.PublishProtectedMethod;
import com.luckmerlin.databinding.ActivityRootFinder;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.Model;
import com.luckmerlin.databinding.ModelClassFinder;

public class ModelActivity extends Activity implements PublishMethods, PublishProtectedMethod {

    public final View getRoot(){
        return new ActivityRootFinder().getActivityFirstRoot(this);
    }

    @Override
    public void onBackPressed() {
        Model model=getModel();
        if (null!=model&&model instanceof OnActivityBackPress&&((OnActivityBackPress)model).onActivityBackPressed(this)){
            return;
        }
        super.onBackPressed();
    }

    public final Model getModel(){
        View root=getRoot();
        MatchBinding matchBinding=null!=root?new ModelClassFinder().findModel(root,null):null;
        Object current=null!=matchBinding?matchBinding.getCurrent():null;
        return null!=current&&current instanceof Model?((Model)current):null;
    }
}
