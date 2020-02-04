package com.merlin.binding;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.activity.BaseActivity;
import com.merlin.classes.Classes;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public final class ClickBinding {
    private final boolean mEnable;
    private final Object mObject;

    protected ClickBinding(boolean enable,Object object){
        mEnable=enable;
        mObject=object;
    }

    public static ClickBinding bind(boolean enable,final Object tag){
        return new ClickBinding(enable,tag);
    }

    protected boolean bind(View view){
        if (null!=view){
            if (mEnable){
//                  Debug.D(getClass(),"DDDDDDDDDDD "+view);
//                  Object last=null,child=view;
//                  do {
//                    if (null!=child&&child instanceof View){
//                        view=(View)child;
//                        ViewDataBinding binding=DataBindingUtil.getBinding(view);
//                        if (null!=binding){
//                            Class cls=binding.getClass().getSuperclass();
//                            Field[] fields=null!=cls?cls.getDeclaredFields():null;
//                            if (null!=fields&&fields.length>0) {
//                                for (Field field:fields) {
//                                    Debug.D(getClass(), "### " + child.getClass() + " " +field.getName()+" "+field.getType());
//                                }
//                            }
//                            Debug.D(getClass(),"#######################");
//                        }
//                    }
//                    last=child;
//                  }while (null!=view&&null!=(child=view.getParent())&&(null==last||last!=child));
                Object tag=mObject;
                Object obj=view;
                ViewDataBinding binding,topBinding=null;
                do {
                    if (null!=obj&&obj instanceof View){
                        binding= DataBindingUtil.getBinding((View) obj);
                        if (null!=binding){
                            topBinding=binding;
                        }
                    }
                }while (null!=obj&&obj instanceof View&&null!=(obj=((View)obj).getParent()));
                View root=null!=topBinding?topBinding.getRoot():null;
                Context context=null!=root?root.getContext():null;
                BaseModel model=null!=context&&context instanceof BaseActivity ?((BaseActivity)context).getViewModel():null;
                WeakReference<BaseModel> reference=new WeakReference<>(model);
                if (null!=model&&model instanceof BaseModel.OnModelViewClick){
                    view.setOnClickListener((v)->{
                        BaseModel bm=null!=reference?reference.get():null;
                        if (null!=bm&&bm instanceof BaseModel.OnModelViewClick){
                            Object object=v.getTag(R.id.resourceId);
                            IDs iDs=null!=object&&object instanceof IDs?((IDs)object):null;
                            ((BaseModel.OnModelViewClick)bm).onViewClick(v, null!=iDs?iDs.getResourceId():v.getId(),tag);
                        }
                    });
                }
            }else{
                view.setOnClickListener(null);
            }
        }
        return false;
    }
}
