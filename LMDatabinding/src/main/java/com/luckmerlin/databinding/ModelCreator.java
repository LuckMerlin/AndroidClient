package com.luckmerlin.databinding;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

class ModelCreator extends ModelClassFinder {

    public final CreatedModel createModel(Context context, Object object,String debug) {
        if (null!=object){
            if (object instanceof CreatedModel){
                return (CreatedModel)object;
            }
            object=object instanceof OnModelResolve ?getIfNotNull(((OnModelResolve)object).onResolveModel(),object):object;
            object=null!=object&&object instanceof Activity ?getIfNotNull(new ActivityRootFinder().getActivityFirstRoot((Activity)object),object):object;
            if (null!=context&&null!=object&&object instanceof Integer){
                object= getIfNotNull(new ViewCreator().create(context,object),object);
            }
            View root=null;
            if (null!=object&&object instanceof View){
                root=(View)object;
                object= DataBindingUtil.getBinding(root);
            }
            object= null!=object&&object instanceof View?getIfNotNull(DataBindingUtil.getBinding((View)object),object):object;
            if (null!=object&&object instanceof ViewDataBinding){
                ViewDataBinding binding=(ViewDataBinding)object;
                root=binding.getRoot();
                MatchBinding matchBinding=findModel((ViewDataBinding)object,null);
                if (null!=matchBinding){
                    Object current=matchBinding.getCurrent();
                    if (null!=current&&current instanceof Model){//Already create and bound
                        return new CreatedModel((Model)current,root,matchBinding);
                    }
                    object=matchBinding;
                }
            }
            object=null!=object&&object instanceof String?getIfNotNull(createClass((String)object),object):object;
            object=null!=object&&object instanceof Class?getIfNotNull(findModel((Class)object,null),object):object;
            if (null==object||!(object instanceof MatchBinding)){
                return null;
            }
            MatchBinding modelBinding=(MatchBinding)object;
            Class modelClass=null!=modelBinding?modelBinding.mType:null;
            if (null==modelClass||!isExistClass(modelClass, Model.class.getName())|| Modifier.isAbstract(modelClass.getModifiers())){
                return null;
            }
            Constructor[] constructors = modelClass.getDeclaredConstructors();
            if (null != constructors && constructors.length > 0) {
                for (Constructor child : constructors) {
                    Class[] types = null != child ? child.getParameterTypes() : null;
                    if (null != types) {
                        boolean access=child.isAccessible();
                        try {
                            child.setAccessible(true);
                            if (types.length == 0) {
                                Debug.D("Create model "+child.getName()+" "+(null!=debug?debug:"."));
                                if (null!=(object = child.newInstance())){//Found model constructor,Now create it
                                    break;
                                }
                            }else if (types.length == 1) {
                                Class typeClass=types[0];
                                if (null!=typeClass&&typeClass.getName().equals(Context.class.getName())){
                                    Debug.D("Create model "+child.getName()+" "+(null!=debug?debug:"."));
                                    if (null!=(object = child.newInstance(context))){//Found model constructor,Now create it
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            //Do nothing
                        }finally {
                            child.setAccessible(access);
                        }
                    }
                }
            }
            Model model=null!=object&&object instanceof Model ?(Model)object:null;
            return null!=root||null!=model?new CreatedModel(model,root,modelBinding):null;
        }
        return null;
    }

    protected final Object getIfNotNull(Object arg,Object def){
        return null!=arg?arg:def;
    }

    protected final Class createClass(String classPkgName){
        if (null!=classPkgName&&classPkgName.length()>0){
            try {
                return Class.forName(classPkgName);
            } catch (ClassNotFoundException e) {
                //Do nothing
            }
        }
        return null;
    }
}
