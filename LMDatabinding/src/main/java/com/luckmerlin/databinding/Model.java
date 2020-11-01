package com.luckmerlin.databinding;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.core.proguard.PublishProtectedMethod;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class Model implements PublishMethods, PublishProtectedMethod {
    private WeakReference<View> mRootView=null;
    private Map<Object,Long> mDispatchHolders;

    public final View findViewById(int viewId,View def){
        return findViewById(getRoot(),viewId,def);
    }

    public final View findViewById(View root,int viewId,View def){
        root=null!=root?root:getRoot();
        return null!=root?root.findViewById(viewId):def;
    }

    public final CharSequence getViewText(View root,int viewId,CharSequence def){
        root=findViewById(root,viewId,null);
        return null!=root&&root instanceof TextView ?((TextView)root).getText():def;
    }

    public final boolean toast(String msg) {
        Context context = getContext();
        if (null != context) {
            if (isMainThread()){
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                return true;
            }
            return post(()->Toast.makeText(context, msg, Toast.LENGTH_SHORT).show(),0);
        }
        return false;
    }

    public final boolean toast(int textId,Object ... args){
        return toast(getString(textId,null,args));
    }

    public final String getString(int textId,String def,Object ... args){
        com.luckmerlin.databinding.Resources resources=new com.luckmerlin.databinding.Resources();
        return null!=resources?resources.getString(getResources(),textId,def,args):def;
    }

    public final android.content.res.Resources getResources(){
        Context context = getContext();
        return null!=context?context.getResources():null;
    }

    public final Model addDispatchHolder(Object object){
        if (null!=object){
            Map<Object,Long> dispatchHolder=mDispatchHolders;
            dispatchHolder=null!=dispatchHolder?dispatchHolder:(mDispatchHolders=new WeakHashMap<>());
            synchronized (dispatchHolder){
                dispatchHolder.put(object,System.currentTimeMillis());
            }
        }
        return this;
    }

    public final Model removeDispatchHolder(Object object){
        Map<Object,Long> dispatchHolder=mDispatchHolders;
        if (null!=dispatchHolder){
            synchronized (dispatchHolder){
                if (null!=object){
                    dispatchHolder.remove(object);
                }
                if (dispatchHolder.size()<=0){
                    mDispatchHolders=null;
                }
            }
        }
        return this;
    }

    private final boolean removeAllDispatchHolder(String debug){
        Map<Object,Long> dispatchHolder=mDispatchHolders;
        if (null!=dispatchHolder){
            synchronized (dispatchHolder){
                dispatchHolder.clear();
                mDispatchHolders=null;
            }
            return true;
        }
        return false;
    }

    public final Collection<Object> getDispatchHolders(){
        Map<Object,Long> dispatchHolder=mDispatchHolders;
        if (null!=dispatchHolder){
            synchronized (dispatchHolder){
                return dispatchHolder.keySet();
            }
        }
        return null;
    }

    public final boolean post(Runnable runnable){
        return post(runnable,0);
    }

    public final boolean post(Runnable runnable,int delay){
        if (null!=runnable){
            delay=delay<=0?0:delay;
            if (isMainThread()&&delay<=0){
                runnable.run();
                return true;
            }else{
                View root=getRoot();
                return null!=root?root.postDelayed(runnable,delay):new Handler(Looper.
                        getMainLooper()).postDelayed(runnable,delay);
            }
        }
        return false;
    }

    public final boolean isMainThread(){
        return isMainThread(Looper.myLooper());
    }

    public final boolean isMainThread(Looper looper){
        Looper mainLooper=Looper.getMainLooper();
        return null!=looper&&null!=mainLooper&&mainLooper==looper;
    }

    public final boolean isRootAttached(){
        return null!=getRoot();
    }

    public final View inflate(int layoutId){
        return inflate(getContext(),layoutId,null);
    }

    public final View inflate(Context context, int layoutId, ViewGroup parent){
        return null!=context?View.inflate(context,layoutId,parent):null;
    }

    public final <T extends Activity> T getActivity(Class<T> cls){
        Context context=getViewContext();
        Activity activity=null!=context&&context instanceof Activity?(Activity)context:null;
        return null!=activity&&(null==cls&&cls.equals(activity.getClass()))?(T)activity:null;
    }

    public final boolean isActivity(Class<? extends Activity> cls){
        return null!=getActivity(cls);
    }

    public final Context getViewContext(){
        View root=getRoot();
        return null!=root?root.getContext():null;
    }

    public final Context getContext(){
        return getViewContext();
    }

    public final Context getApplicationContext(){
        Context context=getContext();
        return null!=context?context.getApplicationContext():null;
    }

    public final Application getApplication(){
        Context context=getApplicationContext();
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    public final boolean finishActivity(String debug){
        return finishActivity(null,null,debug);
    }

    public final boolean finishActivity(Integer resultCode, Intent data, String debug){
        Activity activity=getActivity(null);
        if (null!=activity){
            Debug.D("Finish activity "+activity+" "+resultCode+" "+(null!=debug?debug:"."));
            if (null!=resultCode){
                activity.setResult(resultCode,data);
            }
            activity.finish();
            return true;
        }
        return false;
    }

    public final Intent getActivityIntent(){
        Activity activity= getActivity(null);
        return null!=activity?activity.getIntent():null;
    }

    public final boolean startActivity(Class<? extends Activity> cls, String key,String value){
        Bundle bundle=null;
        if (null!=key){
            bundle=new Bundle();
            bundle.putString(key,value);
        }
        return startActivity(cls,bundle);
    }

    public final boolean startActivity(Class<? extends Activity> cls, Bundle bundle){
        Context context=getContext();
        if (null!=context&&null!=cls){
            Intent intent=new Intent(context,cls);
            if (null!=bundle){
                intent.putExtras(bundle);
            }
            return startActivity(intent,null);
        }
        return false;
    }

    public final boolean startActivity(Class<? extends Activity> cls,Integer forResultCode){
        Context context=getContext();
        return null!=context&&null!=cls&&startActivity(new Intent(context,cls),forResultCode);
    }

    public final boolean startActivity(Intent intent,Integer forResultCode){
        Context context=null!=intent?null!=forResultCode?getViewContext():getContext():null;
        if (null==context){
            Debug.W("Can't start activity with NULL context."+intent+" ");
            return false;
        }
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(null!=forResultCode){
                if ((context instanceof Activity)){
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    Debug.D("Start activity for result."+forResultCode);
                    ((Activity)context).startActivityForResult(intent,forResultCode);
                    return true;
                }else{
                    Debug.W("Will not receive activity result while context is not instanceof activity.");
                }
            }
            context.startActivity(intent);
            return true;
        }catch (Exception e){
            Debug.E("Exception start activity.e="+e+" "+intent,e);
        }
        return false;
    }

    public final int safeCloseIO(Closeable...closeables){
        int succeed=0;
        if (null!=closeables&&closeables.length>0){
            for (Closeable c:closeables) {
                try {
                    if (null!=c){
                        c.close();
                        succeed+=1;
                    }
                } catch (IOException e) {
                    //Do nothing
                }
            }
        }
        return succeed;
    }

    public final ContentResolver getContentResolver(){
        return getContentResolver(null);
    }

    public final ContentResolver getContentResolver(Context context){
        context=null!=context?context:getContext();
        context=null!=context?context:getViewContext();
        return null!=context?context.getContentResolver():null;
    }

    protected void onRootAttached(View view){
        //Do nothing
    }

    protected void onAttachedToWindow(View v){
        //Do nothing
    }

    protected void onDetachedFromWindow(View v){
        //Do nothing
    }

    final boolean attachRoot(View root,String debug){
        if (null!=root){
            Debug.D("Attach model root "+this+" "+(null!=debug?debug:"."));
            mRootView=new WeakReference<>(root);
            onRootAttached(root);
            root.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    onAttachedToWindow(v);
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    root.removeOnAttachStateChangeListener(this);
                    onDetachedFromWindow(v);
                    detachRoot("After root detached from window.");
                }
            });
            return true;
        }
        return false;
    }

    public final View getRoot() {
        WeakReference<View> reference=mRootView;
        View view= null!=reference?reference.get():null;
        if (null==view){
            mRootView=null;
        }
        return view;
    }

    final boolean detachRoot(String debug){
        WeakReference<View> reference=mRootView;
        if (null!=reference){
            Debug.D("Detach model root "+this+" "+(null!=debug?debug:"."));
            mRootView=null;
            View root=reference.get();
            reference.clear();
            removeAllDispatchHolder("While root detach.");
            if (null!=root){
                onRootDetached();
                return true;
            }
            return false;
        }
        return false;
    }

    protected void onRootDetached(){
        //Do nothing
    }

}
