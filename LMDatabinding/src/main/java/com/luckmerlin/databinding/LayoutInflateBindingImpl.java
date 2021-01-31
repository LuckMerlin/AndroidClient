package com.luckmerlin.databinding;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import java.util.Collection;

final class LayoutInflateBindingImpl {

    public boolean inflateLayout(View view, Object layout){
        if (null!=view&&view instanceof ViewGroup &&null!=layout){
            if (layout instanceof Integer){
                if (!layout.equals(Resources.ID_NULL)){
                    DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),(Integer)layout,(ViewGroup)view,true);
                }
            }else if (layout instanceof View&&null==((View)layout).getParent()){
                ViewGroup.LayoutParams params=view.getLayoutParams();
                int width=null!=params?params.width:ViewGroup.LayoutParams.WRAP_CONTENT;
                int height=null!=params?params.height:ViewGroup.LayoutParams.WRAP_CONTENT;
                ((ViewGroup)view).addView((View)layout,new ViewGroup.LayoutParams(width,height));
            }else if (layout instanceof ViewDataBinding){
                View root=((ViewDataBinding)layout).getRoot();
                if (null!=root&&null==root.getParent()){
                    inflateLayout(view,root);
                }
            }else if (layout instanceof Collection &&((Collection)layout).size()>0){
                for (Object child:(Collection)layout) {
                    inflateLayout(view,child);
                }
            }
        }
        return true;
    }
}
