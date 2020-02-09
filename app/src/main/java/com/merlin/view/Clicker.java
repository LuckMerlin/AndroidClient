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
import com.merlin.media.Mode;
import com.merlin.model.Model;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Clicker {
    private Object mListener;

    public Clicker(){
        this(null,true);
    }

    public Clicker(OnTapClick listener, boolean weakListener){
        setListener(listener,weakListener);
    }

    public void setListener(OnTapClick listener, boolean weakListener){
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

    public static Click click(Object arg){
        return click(arg,false);
    }

    public static Click click(Object arg,Integer resourceId){
        return click(arg,resourceId,false);
    }

    public static Click click(Object arg,boolean coverLister){
        return click(arg,null,coverLister);
    }

    public static Click click(Object arg,Integer resourceId,boolean coverLister){
        return new Click(arg,resourceId,coverLister);
    }

    public static class Click{
        public final static int SINGLE_TAP=1231;
        public final static int SINGLE_LONG_CLICK=1232;
        private final int mType=SINGLE_TAP;
        private Object mArg;
        private boolean mCoverExisted;
        private Integer mResourceId;


        public Click(Object arg,Integer resourceId,boolean coverExisted){
            mArg=arg;
            mResourceId=resourceId;
            mCoverExisted=coverExisted;
        }

    }

    public OnTapClick getListener(){
        Object object=mListener;
        object=null!=object&&object instanceof WeakReference?((WeakReference)object).get():object;
        return null!=object&&object instanceof OnTapClick ?(OnTapClick)object:null;
    }

    public boolean attach(View view,boolean enable){
        if (null!=view&&enable){
            return attach(view,new Click(null,null,false));
        }
        return false;
    }

    public boolean attach(View root,Click click){
            if (null==root||null==click){
                return false;
            }
            Integer resourceId=click.mResourceId;
            resourceId=null!=resourceId?resourceId:findViewResourceId(root,null);
            final Res res=new Res(resourceId,click.mArg);
            boolean cover= click.mCoverExisted;
            switch (click.mType){
                case Click.SINGLE_LONG_CLICK:
//                    if (cover){
                        root.setOnLongClickListener(((v)->{
                            return dispatchClickToModel(root,root, ( r, v, model, binding)->{
                                return null!=model&&model instanceof OnLO&&((OnTapClick)model).onTapClick(view,count,resId,arg);
                            });
                        });
//                    }
                    break;
                case Click.SINGLE_TAP:
                    if (cover||!root.hasOnClickListeners()){
                        final int maxInterval=200;
                        final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                            @Override
                            public void run() {
                                int count=mClickCount;
                                mClickCount=0;//Reset
                                Object object=root.getTag(R.id.viewResource);
                                Object arg=null;
                                Integer resId;
                                if (null!=object&&object instanceof Res){
                                    resId=((Res)object).getResourceId();
                                    arg=((Res)object).getArg();
                                }else{
                                    resId=root.getId();
                                }
                                final int resourceId=resId=null==resId?root.getId():resId;
                                OnTapClick listener=getListener();
                                if (null==listener||!listener.onTapClick(root,count,resId,object)){ //Try dispatch to model
                                    final boolean interrupted= dispatchClickToModel(root, root, ( view, root, model, binding)->{
                                        return null!=model&&model instanceof OnTapClick&&((OnTapClick)model).onTapClick(view,count,resourceId,arg);
                                    });
                                    if (!interrupted){
                                        Context context=root.getContext();
                                        if (null!=context&&context instanceof OnTapClick){
                                            ((OnTapClick)context).onTapClick(root,count,resId,arg);
                                        }
                                    }
                                }
                            }
                        };
                        root.setTag(R.id.viewResource,res);
                        root.setOnClickListener((view)->{
                            view.removeCallbacks(multiRunnable);
                            multiRunnable.mView=view;
                            if (multiRunnable.mClickCount==0){
                                multiRunnable.mFirstTime=System.currentTimeMillis();
                            }
                            multiRunnable.mClickCount+=1;
                            view.postDelayed(multiRunnable,maxInterval);
                        });
                    }
                    break;
            }
//        return null!=root&&null!=click&&attach(root,new IDs(findViewResId(root,
//                null==click.mResourceId?root.getId():click.mResourceId),click.mArg),click.mCoverExisted);
        return false;
    }

    private boolean attach(View root, IDs arg, boolean cover){
        if (null!=root){

        }
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
                    OnTapClick listener=getListener();
                    if (null==listener||!listener.onTapClick(root,count,resId,object)){ //Try dispatch to model
                       final boolean interrupted= dispatchClickToModel(root, root, ( view, root, model, binding)->{
                           return null!=model&&model instanceof OnTapClick&&((OnTapClick)model).onTapClick(view,count,resId,arg);
                       });
                        if (!interrupted){
                            Context context=root.getContext();
                            if (null!=context&&context instanceof OnTapClick){
                                ((OnTapClick)context).onTapClick(root,count,resId,arg);
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

    private boolean dispatchClickToModel(View view,View root,Dispatcher dispatcher){
        if (null!=root&&null!=dispatcher){
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
                                if (null!=data&&data instanceof Mode&&dispatcher.onDispatch(view,root,(Model)data,binding)){
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
                return dispatchClickToModel(view,(View)parent,dispatcher);
            }
            return interrupted;
        }
        return false;
    }

    private interface Dispatcher{
        boolean onDispatch(View view, View root, Model model,ViewDataBinding binding);
    }

    private static abstract class MultiClickRunnable implements Runnable{
        View mView;
        long mFirstTime;
        int mClickCount;
    }


    private Integer findViewResourceId(View view,Integer def){
        if (null!=view){
            int resourceId= Resources.ID_NULL;
            try {
                if (view instanceof TextView&&null!=((TextView)view).getText()){
                    Field field=TextView.class.getDeclaredField("mTextId");
                    field.setAccessible(true);
                    Object object=field.get(view);
                    resourceId=null!=object&&object instanceof Integer?(Integer)object:resourceId;
                }else if (view instanceof ImageView){
                    Field field=ImageView.class.getDeclaredField("mResource");
                    field.setAccessible(true);
                    Object object=field.get(view);
                    resourceId=null!=object&&object instanceof Integer?(Integer)object:def;
                }
            }catch (Exception e){
                //Do nothing
            }
            return resourceId!=Resources.ID_NULL?resourceId:def;
        }
        return null;
    }

}
