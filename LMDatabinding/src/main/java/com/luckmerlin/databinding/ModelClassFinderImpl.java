package com.luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.match.Matchable;

class ModelClassFinderImpl extends BindingMethodIterator {

    public final MatchBinding find(ViewDataBinding binding, Matchable matchable){
        return find(binding,matchable,null);
    }

    public final MatchBinding find(Class bindingClass,Matchable matchable,Class cls){
        return super.iterate(bindingClass,(Object o)-> null!=o&&o instanceof MatchBinding?matchBinding((MatchBinding)o,matchable,cls):Matchable.CONTINUE);
    }

    public final MatchBinding find(ViewDataBinding binding,Matchable matchable,Class cls){
        return super.iterate(binding,(Object o)-> null!=o&&o instanceof MatchBinding?matchBinding((MatchBinding)o,matchable,cls):Matchable.CONTINUE);
    }

    private Integer matchBinding(MatchBinding matchBinding,Matchable matchable,Class cls){
        if (null!=matchBinding){
            Class matchClass=matchBinding.mType;
            if (null==cls||(null!=matchClass&&isExistClass(matchClass,cls.getName()))){
                if (null==matchable){
                    return Matchable.MATCHED;
                }
                return matchable.onMatch(matchBinding);
            }
        }
        return null;
    }

    public final MatchBinding findModel(ViewDataBinding binding,Matchable matchable){
        return find(binding,matchable, LModel.class);
    }

    public final boolean isExistClass(Class srcClass,final String clsTargetName){
        if (null!=srcClass&&null!=clsTargetName){
            while (null!=srcClass){
                String clsName=null!=srcClass?srcClass.getName():null;
                if (null!=clsName&&clsName.equals(clsTargetName)){
                    return true;
                }
                srcClass=srcClass.getSuperclass();
            }
        }
        return false;
    }

}
