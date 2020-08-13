package com.luckmerlin.mvvm;

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

public abstract class Model implements PublishMethods, PublishProtectedMethod {
    public static WeakReference<View> mRootView=null;

    public Model(){

    }

    protected final boolean isRootAttached(){
        return null!=getRoot();
    }

    protected final View getRoot() {
        WeakReference<View> reference=mRootView;
        View view= null!=reference?reference.get():null;
        if (null==view){
            mRootView=null;
        }
        return view;
    }

    protected void onRootAttached(View view){
        //Do nothing
    }

    final boolean attachRoot(View root,String debug){
        if (null!=root){
            mRootView=new WeakReference<>(root);
            onRootAttached(root);
            return true;
        }
        return false;
    }

    final boolean dettachRoot(String debug){
        WeakReference<View> reference=mRootView;
        if (null!=reference){
            View root=reference.get();
            reference.clear();
            mRootView=null;
            if (null!=root){
                onRootDettached();
                return true;
            }
            return false;
        }
        return false;
    }

    protected final View findViewById(int viewId,View def){
        return findViewById(getRoot(),viewId,def);
    }

    protected final View findViewById(View root,int viewId,View def){
        root=null!=root?root:getRoot();
        return null!=root?root.findViewById(viewId):def;
    }

    protected final CharSequence getViewText(View root,int viewId,CharSequence def){
        root=findViewById(root,viewId,null);
        return null!=root&&root instanceof TextView?((TextView)root).getText():def;
    }

    protected final boolean toast(String msg) {
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

    protected final boolean post(Runnable runnable){
        return post(runnable,0);
    }

    protected final boolean post(Runnable runnable,int delay){
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

    protected final boolean isMainThread(){
        return isMainThread(Looper.myLooper());
    }

    protected final boolean isMainThread(Looper looper){
        Looper mainLooper=Looper.getMainLooper();
        return null!=looper&&null!=mainLooper&&mainLooper==looper;
    }

    protected final View inflate(int layoutId){
        return inflate(getContext(),layoutId,null);
    }

    protected final View inflate(Context context, int layoutId, ViewGroup parent){
        return null!=context?View.inflate(context,layoutId,parent):null;
    }

    protected final <T extends Activity> T getActivity(Class<T> cls){
        Context context=getViewContext();
        Activity activity=null!=context&&context instanceof Activity?(Activity)context:null;
        return null!=activity&&(null==cls&&cls.equals(activity.getClass()))?(T)activity:null;
    }

    protected final boolean isActivity(Class<? extends Activity> cls){
        return null!=getActivity(cls);
    }

    protected final Context getViewContext(){
        View root=getRoot();
        return null!=root?root.getContext():null;
    }

    protected final Context getContext(){
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

    protected final void onRootDettached(){
        //Do nothing
    }

    protected final Intent getActivityIntent(){
       Activity activity= getActivity(null);
        return null!=activity?activity.getIntent():null;
    }

    protected final boolean startActivity(Class<? extends Activity> cls, String key,String value){
        Bundle bundle=null;
        if (null!=key){
            bundle=new Bundle();
            bundle.putString(key,value);
        }
        return startActivity(cls,bundle);
    }

    protected final boolean startActivity(Class<? extends Activity> cls, Bundle bundle){
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

    protected final boolean startActivity(Class<? extends Activity> cls,Integer forResultCode){
        Context context=getContext();
        return null!=context&&null!=cls&&startActivity(new Intent(context,cls),forResultCode);
    }

    protected final boolean startActivity(Intent intent,Integer forResultCode){
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

    protected final int safeCloseIO(Closeable ...closeables){
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

    protected final ContentResolver getContentResolver(){
        return getContentResolver(null);
    }

    protected final ContentResolver getContentResolver(Context context){
        context=null!=context?context:getContext();
        context=null!=context?context:getViewContext();
        return null!=context?context.getContentResolver():null;
    }


}
