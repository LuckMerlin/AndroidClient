package com.luckmerlin.databinding.ui;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Adapter;
import android.widget.AdapterView;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.databinding.DataBindingUtil;

import java.lang.reflect.Method;

public final class Iterator extends DataBindingUtil {

    public boolean dispatch(View view, boolean recursion, Matchable matchable){
        return dispatch(view,null,recursion,matchable);
    }

    public boolean dispatch(View view,Object object,boolean recursion, Matchable matchable){
        if (null!=object&&isMatched(matchable.onMatch(object))){
            return true;
        }else if (!recursion){
            return false;
        }else if (dispatchToModel(view,matchable)){
            return true;
        }
        Context context=view.getContext();
        if (null==context){
            return false;
        }else if (isMatched(matchable.onMatch(context))){
            return true;
        }else if (context instanceof Activity){
            Activity activity=(Activity)context;
//            View root=view.getRootView();
//            Window window=null!=activity&&null!=root?activity.getWindow():null;
//            View decorView=null!=window?window.getDecorView():null;
//            if (null!=decorView&&null!=root&&(decorView!=root) &&
//                    dispatchToModel(decorView,matchable,false)){
//                return true;
//            }
        }else if (context instanceof ContextThemeWrapper){
            Context innerContext=((ContextThemeWrapper)context).getBaseContext();
            if (null!=innerContext&&isMatched(matchable.onMatch(innerContext))){
                return true;
            }
        }
        context=context.getApplicationContext();
        if (null!=context&&isMatched(matchable.onMatch(context))){
            return true;
        }
        return false;
    }

    final boolean dispatchToModel(final View view,Matchable matchable){
        if (null==view||null==matchable){
            return false;
        }else if (isMatched(matchable.onMatch(view))){//To self
            return true;
        }
        Object viewBinding=getBinding(view);
        Class bindingClass=null!=viewBinding?viewBinding.getClass():null;

        Object adapter=view instanceof AdapterView ?((AdapterView)view).getAdapter():null;
        if (null!=adapter&&isMatched(matchable.onMatch(adapter))){//To adapter
            return true;
        } else if (view instanceof ViewGroup){
            adapter=invoke("androidx.recyclerview.widget.RecycleView","getAdapter",view);
            if (null!=adapter&&isMatched(matchable.onMatch(adapter))){//To RecycleView adapter
                return true;
            }
        }
        ViewParent parent=view.getParent();
        return null!=parent&&parent instanceof View&&dispatchToModel((View)parent,matchable);
    }

    final boolean isMatched(Integer match){
        return null!=match&&match==Matchable.MATCHED;
    }

    final Object invoke(String clsName,String methodName,Object object,Object... args){
        try {
            if (null!=clsName&&null!=methodName&&clsName.length()>0&&methodName.length()>0&&null!=object){
                Class cls=Class.forName(clsName);
                Method method=null!=cls?cls.getDeclaredMethod(methodName):null;
                return null!=method?method.invoke(object,args):null;
            }
        } catch (Exception e) {
            //Do nothing
        }
        return null;
    }
}
