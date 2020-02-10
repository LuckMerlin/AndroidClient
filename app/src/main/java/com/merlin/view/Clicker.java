package com.merlin.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.binding.IDs;
import com.merlin.classes.Classes;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Mode;
import com.merlin.model.Model;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Clicker {
    private Object mListener;
    public final static int SINGLE_TAP_MASK=0x01;//0001
    public final static int SINGLE_LONG_CLICK_MASK=0x02;//0010

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
        return click(SINGLE_TAP_MASK,arg,resourceId,false);
    }

    public static Click click(Object arg,boolean coverLister){
        return click(SINGLE_TAP_MASK,arg,coverLister);
    }

    public static Click click(int type,Object arg,boolean coverLister){
        return click(type,arg,null,coverLister);
    }

    public static Click click(int type,Object arg,Integer resourceId,boolean coverLister){
        return new Click(type,arg,resourceId,coverLister);
    }

    public static Click longClick(Object arg){
        return longClick(arg,false);
    }

    public static Click longClick(Object arg,Integer resourceId){
        return longClick(arg,resourceId,false);
    }

    public static Click longClick(Object arg,boolean coverLister){
        return longClick(arg,null,coverLister);
    }

    public static Click longClick(Object arg,Integer resourceId,boolean coverLister){
        return new Click(SINGLE_LONG_CLICK_MASK,arg,resourceId,coverLister);
    }

    public static boolean setInterrupterTag(View view,Object object){
        if (null!=view){
            object=null!=object?new WeakReference<>(object):null;
            view.setTag(R.id.interruptClick,object);
            return true;
        }
        return false;
    }


    public static class Click{
        private final int mType;
        private Object mArg;
        private boolean mCoverExisted;
        private Integer mResourceId;

        public Click(Object arg,Integer resourceId,boolean coverExisted){
            this(SINGLE_TAP_MASK,arg,resourceId,coverExisted);
        }

        public Click(int type,Object arg,Integer resourceId,boolean coverExisted){
            mType=type;
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
            if ((click.mType&SINGLE_LONG_CLICK_MASK)>0){
//                    if (cover){
                root.setTag(R.id.viewResource,res);
                root.setOnLongClickListener(((v)->{
                    Object object=root.getTag(R.id.viewResource);
                    Object arg=null;
                    Integer resId;
                    if (null!=object&&object instanceof Res){
                        resId=((Res)object).getResourceId();
                        arg=((Res)object).getArg();
                    }else{
                        resId=root.getId();
                    }
                    final int resIdFinal=null==resId?root.getId():resId;
                    final Object argFinal=arg;
                    final boolean interrupted=dispatchClickToModel(root,root, ( r, vi, model, binding)->{
                        return null!=model&&model instanceof OnLongClick&&((OnLongClick)model)
                                .onLongClick(root,1,resIdFinal,argFinal);
                    });
                    Context context=null!=root?root.getContext():null;
                    if (null!=context&&context instanceof OnLongClick){
                        return ((OnLongClick)context).onLongClick(root,1,resIdFinal,argFinal);
                    }
                    return interrupted;
                }));

//                    }
            }
            if ((click.mType&SINGLE_TAP_MASK)>0){
                if (cover||!root.hasOnClickListeners()){
                    final int maxInterval=200;
                    final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                        @Override
                        public void run() {
                            final int count=mClickCount;
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
                            final int resourceIdFinal=resId=null==resId?root.getId():resId;
                            final Object argFinal=arg;
                            OnTapClick listener=getListener();
                            if (null==listener||!listener.onTapClick(root,count,resId,object)){ //Try dispatch to model
                                final boolean interrupted= dispatchClickToModel(root, root, ( view, rt, model, binding)->{
                                     return null!=model&&model instanceof OnTapClick&&((OnTapClick)model).
                                            onTapClick(root,count,resourceIdFinal,argFinal);
                                });
                                if (!interrupted){
                                    Context context=root.getContext();
                                    if (null!=context&&context instanceof OnTapClick){
                                        ((OnTapClick)context).onTapClick(root,count,resourceIdFinal,argFinal);
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
                    return true;
                }
            }
//        return null!=root&&null!=click&&attach(root,new IDs(findViewResId(root,
//                null==click.mResourceId?root.getId():click.mResourceId),click.mArg),click.mCoverExisted);
        return false;
    }

    private boolean dispatchClickToModel(View view,View root,Dispatcher dispatcher){
        if (null!=view&&null!=dispatcher){
            ViewDataBinding binding=DataBindingUtil.getBinding(view);
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
                                if (null!=data&&data instanceof Model&&dispatcher.onDispatch(view,root,(Model)data,binding)){
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
            ViewParent parent=interrupted?null:view.getParent();
            if (null!=parent&&parent instanceof View &&(parent!=view)){
                Object interrupter=((View)parent).getTag(R.id.interruptClick);
                if (null!=interrupter){
                    interrupter=interrupter instanceof WeakReference?((WeakReference)interrupter).get():null;
                    Debug.D(getClass(),"DDDDinterrupterDDDd "+interrupter);
                    if (interrupter instanceof Model){
                        return dispatcher.onDispatch(view,root,(Model)interrupter,binding);
                    }else if (interrupter instanceof View){
                        return dispatchClickToModel((View)interrupter,root,dispatcher);
                    }
                }
                Debug.D(getClass(),"DDDparentDDDDd "+parent);
                return dispatchClickToModel((View)parent,root,dispatcher);
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
            Integer resourceId=null;
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
                    resourceId=null!=object&&object instanceof Integer?(Integer)object:resourceId;
                }
            }catch (Exception e){
                //Do nothing
            }
            return null!=resourceId&&resourceId!=Resources.ID_NULL?resourceId:def;
        }
        return null;
    }

}
