package com.luckmerlin.databinding;

import android.view.View;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.core.proguard.PublishMethods;
/**
 * @deprecated
 */
public class ModelClassFinder extends BindingMethodIterator implements PublishMethods {

    public final MatchBinding findModel(View modelRoot, Matchable matchable){
        if (null!=modelRoot&&DataBindingUtil.checkDataBindingEnable(false)){
            ViewDataBinding binding=DataBindingUtil.getBinding(modelRoot);
            MatchBinding matchBinding=null!=binding?findModel(binding,matchable):null;
            if (null!=matchBinding&&null==matchBinding.mViewBinding){
                matchBinding=new MatchBinding(matchBinding.mSetMethod,matchBinding.mGetMethod,
                        matchBinding.mType,matchBinding.getCurrent(),binding);
            }
            return matchBinding;
        }
        return null;
    }

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
                    return Matchable.MATCHED;//Must keep return not NULL
                }
                Integer matched= matchable.onMatch(matchBinding);
                return null!=matched?matched:Matchable.CONTINUE;//Must keep return not NULL
            }
        }
        return Matchable.CONTINUE;//Must keep return not NULL
    }

    public final MatchBinding findModel(ViewDataBinding binding,Matchable matchable){
        return find(binding,matchable, Model.class);
    }

    public final MatchBinding findModel(Class modelClass, Matchable matchable){
        return find(modelClass,matchable,Model.class);
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
