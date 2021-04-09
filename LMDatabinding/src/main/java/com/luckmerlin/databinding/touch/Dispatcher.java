package com.luckmerlin.databinding.touch;

import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import com.luckmerlin.databinding.DataBindingUtil;
import java.lang.reflect.Field;

/**
 * @deprecated
 */
public class Dispatcher {

    public boolean dispatch(View view,boolean dispatch, Matchable matchable){
        return dispatch(view,null,dispatch,matchable);
    }

    public boolean dispatch(View view,Object object,boolean dispatch, Matchable matchable){
            if (null!=matchable&&null!=view){
                if (null!=object&&isMatched(matchable.onMatch(object))){
                    return true;
                }else if (!dispatch){
                    return false;
                }else if (dispatchToModel(view,matchable,true)){
                    return true;
                }
                Context context=view.getContext();
                if (null==context){
                    return false;
                }else if (isMatched(matchable.onMatch(context))){
                    return true;
                }else if (context instanceof Activity){
                    Activity activity=(Activity)context;
                    View root=view.getRootView();
                    Window window=null!=activity&&null!=root?activity.getWindow():null;
                    View decorView=null!=window?window.getDecorView():null;
                    if (null!=decorView&&null!=root&&(decorView!=root) &&dispatchToModel(decorView,matchable,false)){
                        return true;
                    }
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
            }
            return false;
    }

    private final boolean isMatched(Integer match){
        return null!=match&&match==Matchable.MATCHED;
    }

    private boolean dispatchToModel(View view, Matchable matchable,boolean direction){
        if (null!=view&&null!=matchable){
            Adapter adapter=view instanceof AdapterView ?((AdapterView)view).getAdapter():null;
            if (null!=adapter&&isMatched(matchable.onMatch(adapter))){
                return true;
            }
            Object reAdapter=view instanceof RecyclerView ?((RecyclerView)view).getAdapter():null;
            if (null!=reAdapter&&isMatched(matchable.onMatch(reAdapter))){
                return true;
            }
            ViewDataBinding binding= DataBindingUtil.getBinding(view);
            Class bindingClass=null!=binding?binding.getClass():null;
            bindingClass=null!=bindingClass?bindingClass.getSuperclass():null;
            Field[] fields=null!=bindingClass?bindingClass.getDeclaredFields():null;
            if (null!=fields&&fields.length>0){
                for (Field child:fields) {
                    if (null==child){
                        continue;
                    }
                    boolean isAccessible=child.isAccessible();
                    try {
                        if (!isAccessible){
                            child.setAccessible(true);
                        }
                        if (isMatched(matchable.onMatch(child.get(binding)))){
                            return true;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }finally {
                        if (!isAccessible){
                            child.setAccessible(isAccessible);
                        }
                    }
                }
            }
            if (direction){
                ViewParent parent=view.getParent();
                return null!=parent&&parent instanceof View&&dispatchToModel((View)parent,matchable,true);
            }else if (view instanceof ViewGroup){
                ViewGroup vg=(ViewGroup)view;
                int count=vg.getChildCount();
                for (int i = 0; i < count; i++) {
                    if (dispatchToModel(vg.getChildAt(i),matchable,false)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
