package com.merlin.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.binding.IDs;
import com.merlin.classes.Classes;
import com.merlin.client.R;
import com.merlin.model.Model;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class MultiClicker {
    private Object mListener;

    public MultiClicker(){
        this(null,true);
    }

    public MultiClicker(OnMultiClick listener, boolean weakListener){
        setListener(listener,weakListener);
    }

    public void setListener(OnMultiClick listener, boolean weakListener){
        Object object=mListener;
        if (null!=object){
            mListener=null;
            if (object instanceof WeakReference){
                ((WeakReference)object).clear();
            }
        }
        if (null!=listener){
            mListener=weakListener?new WeakReference<>(listener):listener;
        }
    }

    public static MultiClick multi(Object arg){
        return multi(arg,false);
    }

    public static MultiClick multi(Object arg,boolean coverLister){
        return new MultiClick(arg,coverLister);
    }

    public static class MultiClick{
        private Object mArg;
        private boolean mCoverExisted;

        public MultiClick(Object arg,boolean coverExisted ){
            mArg=arg;
            mCoverExisted=coverExisted;
        }
    }

    public OnMultiClick getListener(){
        Object object=mListener;
        object=null!=object&&object instanceof WeakReference?((WeakReference)object).get():object;
        return null!=object&&object instanceof OnMultiClick ?(OnMultiClick)object:null;
    }

    public boolean attach(View view,boolean enable){
        if (null!=view&&enable){
            return attach(view,new MultiClick(null,false));
        }
        return false;
    }

    public boolean attach(View root,MultiClick click){
        return null!=root&&null!=click&&attach(root,new IDs(findViewResId(root,Resources.ID_NULL),click.mArg),click.mCoverExisted);
    }

    public boolean attach(View view,IDs arg){
        return attach(view,arg,false);
    }

    public boolean attach(View root, IDs arg, boolean cover){
        if (null!=root&&(cover||!root.hasOnClickListeners())){
            final int maxInterval=400;
            final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                @Override
                public void run() {
                    int count=mClickCount;
                    mClickCount=0;//Reset
                    Object object=root.getTag(R.id.resourceId);
                    int resId;
                    if (null!=object&&object instanceof IDs){
                        resId=((IDs)object).getResourceId();
                        object=((IDs)object).getArg();
                    }else{
                        resId=root.getId();
                    }
                    OnMultiClick listener=getListener();
                    if (null==listener||!listener.onMultiClick(root,count,resId,object)){ //Try dispatch to model
                        if (!dispatchMultiClickToModel(root,root,count,resId,object)){
                            Context context=root.getContext();
                            if (null!=context&&context instanceof OnMultiClick){
                                ((OnMultiClick)context).onMultiClick(root,count,resId,arg);
                            }
                        }
                    }
                }
            };
            root.setTag(R.id.resourceId,arg);
            root.setOnClickListener((view)->{
                view.removeCallbacks(multiRunnable);
                multiRunnable.mView=view;
                if (multiRunnable.mClickCount==0){
                    multiRunnable.mFirstTime=System.currentTimeMillis();
                }
                multiRunnable.mClickCount+=1;
                view.postDelayed(multiRunnable,maxInterval);
            });
            return true;
        }
        return false;
    }

    private boolean dispatchMultiClickToModel(View view,View root,int count,int resId, Object arg){
        if (null!=root){
            ViewDataBinding binding=DataBindingUtil.getBinding(root);
            Class cls=null!=binding?binding.getClass().getSuperclass():null;
            Method[] methods=null!=cls?cls.getDeclaredMethods():null;
            boolean interrupted=false;
            if (null!=methods&&methods.length>0){
                Class type;
                for (Method method:methods) {
                    if (null!=method&&null!=(type=method.getReturnType())){
                        Class[] types=method.getParameterTypes();
                        if ((null==types||types.length<=0)&&Classes.isAssignableFrom(type,Model.class)){
                            method.setAccessible(true);
                            try {
                                Object data=method.invoke(binding);
                                if (null!=data&&data instanceof Model&&data instanceof OnMultiClick&&
                                        ((OnMultiClick)data).onMultiClick(view,count,resId,arg)){
                                    interrupted=true;
                                    break;
                                }
                            } catch (Exception e) {
                               //Do nothing
                            }
                        }
                    }
                }
            }
            ViewParent parent=interrupted?null:root.getParent();
            if (null!=parent&&parent instanceof View &&(parent!=root)){
                return dispatchMultiClickToModel(view,(View)parent,count,resId,arg);
            }
            return interrupted;
        }
        return false;
    }

    private static abstract class MultiClickRunnable implements Runnable{
        View mView;
        long mFirstTime;
        int mClickCount;
    }


    private int findViewResId(View view,int def){
        if (null!=view){
            int resourceId= Resources.ID_NULL;
            try {
                if (view instanceof TextView&&null!=((TextView)view).getText()){
                    Field field=TextView.class.getDeclaredField("mTextId");
                    field.setAccessible(true);
                    Object object=field.get(view);
                    resourceId=null!=object&&object instanceof Integer?(Integer)resourceId:resourceId;
                }else if (view instanceof ImageView){
                    Field field=ImageView.class.getDeclaredField("mResource");
                    field.setAccessible(true);
                    Object object=field.get(view);
                    resourceId=null!=object&&object instanceof Integer?(Integer)resourceId:resourceId;
                }
            }catch (Exception e){
                //Do nothing
            }
            return resourceId!=Resources.ID_NULL?resourceId:def;
        }
        return def;
    }
}
