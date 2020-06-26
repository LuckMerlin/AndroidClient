package com.merlin.click;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.Res;
import com.merlin.lib.R;
import com.merlin.model.Model;
import com.merlin.model.ModelBinder;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

    public static Click multiClick(Object arg,boolean coverLister){
        return multiClick(arg,null,coverLister);
    }

    public static Click multiClick(Object arg,Integer resourceId,boolean coverLister){
        return click(SINGLE_TAP_MASK|SINGLE_LONG_CLICK_MASK,arg,resourceId,coverLister);
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
        return setInterrupterTag(view,object,true);
    }

    public static boolean setInterrupterTag(View view,Object object,boolean weak){
        if (null!=view){
            object=null!=object?weak?new WeakReference<>(object):object:null;
            view.setTag(R.id.interruptClick,object);
            return true;
        }
        return false;
    }

    public static Object getInterrupterTag(View view,Object def){
        Object tag=null!=view?view.getTag(R.id.interruptClick):def;
        return null!=tag?tag:def;
    }

    public static Res getRes(View view, Res def){
        Object object=view.getTag(R.id.viewResource);
        return null!=object&&object instanceof Res?(Res)object:def;
    }

    public static boolean putRes(View view,Res res){
         if (null!=view){
             final Res existedRes=getRes(view,null);
             if (null==res){
                 if (null!=existedRes){
                     view.setTag(R.id.viewResource,null);
                     return true;
                 }
             }else{
                 Integer resourceId=res.getResourceId();
                 Object arg=res.getArg();
                 resourceId=null!=resourceId&&resourceId!=Resources.ID_NULL?resourceId:
                         (null!=existedRes?existedRes.getResourceId():null);
                 arg=(null!=arg?arg:(null!=existedRes?existedRes.getArg():null));
                 view.setTag(R.id.viewResource,new Res(resourceId,arg));
             }
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
                Clicker.putRes(root,res);
                root.setOnLongClickListener(((v)->{
                    Res longRes=getRes(root,null);
                    Object arg=null;
                    Integer resId;
                    if (null!=longRes){
                        resId=longRes.getResourceId();
                        arg=longRes.getArg();
                    }else{
                        resId=root.getId();
                    }
                    final int resIdFinal=null==resId?root.getId():resId;
                    final Object argFinal=arg;
                    final List<Object> dispatched=new ArrayList<>(10);
                    final boolean interrupted=dispatchClickToModel(root,root, ( r, vi, model, binding)->{
                        if (null!=model&&model instanceof OnLongClick&&!dispatched.contains(model)){
                            dispatched.add(model);
                            return ((OnLongClick)model).onLongClick(root,1,resIdFinal,argFinal);
                        }
                        return false;
                    });
                    Context context=!interrupted&&null!=root?root.getContext():null;
                    OnLongClick longClick=null!=context&&context instanceof OnLongClick&&!dispatched.contains(context)?(OnLongClick)context:null;
                    dispatched.clear();
                    return null!=longClick&&longClick.onLongClick(root,1,resIdFinal,argFinal);
                }));

//                    }
            }
            if ((click.mType&SINGLE_TAP_MASK)>0){
                if (cover||!root.hasOnClickListeners()){
                    final int maxInterval=300;
                    final MultiClickRunnable multiRunnable=new MultiClickRunnable(){
                        @Override
                        public void run() {
                            final int count=mClickCount;
                            mClickCount=0;//Reset
                            Res clickRes=getRes(root,null);
                            Object arg=null;
                            Integer resId;
                            if (null!=clickRes){
                                resId=clickRes.getResourceId();
                                arg=clickRes.getArg();
                            }else{
                                resId=root.getId();
                            }
                            final int resourceIdFinal=resId=null==resId?root.getId():resId;
                            final Object argFinal=arg;
                            OnTapClick listener=getListener();
                            final List<Object> dispatched=new ArrayList<>(10);
                            if (null==listener||!listener.onTapClick(root,count,resId,argFinal)){ //Try dispatch to model
                                final boolean interrupted= dispatchClickToModel(root, root, ( view, rt, model, binding)->{
                                    if (null!=model&&model instanceof OnTapClick&&!dispatched.contains(model)){
                                        dispatched.add(model);
                                        return ((OnTapClick)model).onTapClick(root,count,resourceIdFinal,argFinal);
                                    }
                                    return false;
                                });
                                Context context=!interrupted?root.getContext():null;
                                if (null!=context&&context instanceof OnTapClick&&!dispatched.contains(context)){
                                    ((OnTapClick)context).onTapClick(root,count,resourceIdFinal,argFinal);
                                }
                                dispatched.clear();
                            }
                        }
                    };
                    Clicker.putRes(root,res);
                    long[] downTime=new long[1];
                    root.setOnTouchListener(( v, event)->{
                        if (event.getAction()==MotionEvent.ACTION_DOWN){
                            downTime[0]=System.currentTimeMillis();
                        }
                        return false;
                    });
                    root.setOnClickListener((view)->{
                        long duration=System.currentTimeMillis()-downTime[0];
                        if (duration<500){
                            view.removeCallbacks(multiRunnable);
                            multiRunnable.mView=view;
                            if (multiRunnable.mClickCount==0){
                                multiRunnable.mFirstTime=System.currentTimeMillis();
                            }
                            multiRunnable.mClickCount+=1;
                            view.postDelayed(multiRunnable,maxInterval);
                        }
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
            if (dispatcher.onDispatch(view,root,view,binding)){
                return true;
            }
            Object modelBind=view.getTag(R.id.modelBind);
            if (null!=modelBind&& modelBind instanceof Model&&dispatcher.onDispatch(view,root,modelBind,binding)){
                return true;
            }
            RecyclerView.Adapter adapter=view instanceof RecyclerView?((RecyclerView)view).getAdapter():null;
            if (dispatcher.onDispatch(view,root,adapter,binding)){
                return true;
            }
            ViewParent parent=view.getParent();
            if (null!=parent&&parent instanceof View &&(parent!=view)){
                modelBind=((View)parent).getTag(R.id.modelBind);
                if (null!=modelBind&& modelBind instanceof Model&&dispatcher.onDispatch(view,root,modelBind,binding)){
                    return true;
                }
                Object interrupter=((View)parent).getTag(R.id.interruptClick);
                interrupter=null!=interrupter?interrupter instanceof WeakReference?((WeakReference)interrupter).get():interrupter:null;
                if (null!=interrupter){
                   if (interrupter instanceof View){
                        return dispatchClickToModel((View)interrupter,root,dispatcher);
                   }else{
                       return dispatcher.onDispatch(view,root,interrupter,binding);
                   }
                }
                if (((View)parent).getId()==android.R.id.content&&parent instanceof ViewGroup){//Give up
                    ViewGroup vg=(ViewGroup)parent;
                    int count=null!=vg?vg.getChildCount():0;
                    View child;
                    for (int i=0;i<count;i++){
                        if (null!=(child=vg.getChildAt(i))){
                            Model model= ModelBinder.getBindModel(child);
                            if (null!=model&&dispatcher.onDispatch(child,root,model,binding)){
                                break;
                            }
                        }
                     }
                    return true;
                }
                return dispatchClickToModel((View)parent,root,dispatcher);
            }
            return false;
        }
        return false;
    }

    private interface Dispatcher{
        boolean onDispatch(View view, View root, Object model, ViewDataBinding binding);
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
