package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.merlin.binding.StatusBar;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.retrofit.Retrofit;
import com.merlin.view.StatusBarLayout;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class Model {
    private WeakReference<View> mRootView=null;

    public final View getRoot() {
        WeakReference<View> reference=mRootView;
        return null!=reference?reference.get():null;
    }

    protected void onRootAttached(View root){
        //Do nothing
    }

    private boolean initial(View view){
        if (null!=view){
            mRootView=new WeakReference<>(view);
            onRootAttached(view);
            return true;
        }
        return false;
    }

    public final boolean toast(int textResId){
        return toast(textResId,null);
    }

    public final String getText(int textResId){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        return null!=context?context.getResources().getString(textResId):null;
    }

    public final boolean toast(int textResId,String note){
        String text=getText(textResId);
        return toast((null!=text?text:"")+(null!=note?note:""));
    }

    public final boolean toast(String msg){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&null!=msg){
            if (Looper.getMainLooper()==Looper.myLooper()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }else{
                root.post(()->Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
            }
            return true;
        }
        return false;
    }

    public final View findViewById(int id){
        return findViewById(id,View.class);
    }

    public final <T> T findViewById(int id,Class<T> cls){
        View root=getRoot();
        View child=null!=root?root.findViewById(id):null;
        return null!=child&&null!=cls?(T)child:null;
    }

    public final Context getViewContext(){
        WeakReference<View> reference=mRootView;
        View view= null!=reference?reference.get():null;
        return null!=view?view.getContext():null;
    }

    public final Context getContext(){
        Context context=getViewContext();
        return null!=context?context.getApplicationContext():null;
    }

    protected final boolean runOnUiThread(Runnable runnable){
        return null!=runnable&&post(runnable,0);
    }

    protected final boolean post(Runnable runnable){
        return post(runnable,0);
    }

    protected final boolean post(Runnable runnable,int delay){
        if (null!=runnable){
            View root=getRoot();
            if (null==root){
                return new Handler(Looper.getMainLooper()).postDelayed(runnable,delay);
            }
            return root.postDelayed(runnable,delay);
        }
        return false;
    }

    protected final Application getApplication(){
        Context context=getContext();
        context=null!=context?context instanceof Application?context:context.getApplicationContext():null;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    protected final List<Activity> finishAllActivity(Object...activities){
        Application application=getApplication();
        return null!=application?application.finishAllActivity(activities):null;
    }

    public final boolean finishActivity(){
        Activity activity=getActivity(null);
        if (null!=activity){
            activity.finish();
        }
        return false;
    }

    public final <T extends Activity> Activity getActivity(Class<T> cls){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&context instanceof Activity){
            return (Activity)context;
        }
        return null;
    }

    protected final boolean startActivity(Class<? extends Activity> cls){
        return startActivityWithBundle(cls,null);
    }

    protected final boolean startActivityWithBundle(Class<? extends Activity> cls, Bundle bundle){
        Context context=getContext();
        if (null!=context&&null!=cls){
            Intent intent=new Intent(context,cls);
            if (null!=bundle){
                intent.putExtras(bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(intent);
                return true;
            }catch (Exception e){
                Debug.E(getClass(),"Fail start activity.e="+e+" "+cls,e);
            }
            return false;
        }
        return false;
    }

    protected final LifecycleProvider getLifecycleProvider(){
        View view=getRoot();
        Context context=null!=view?view.getContext():null;
        if (null!=context&&context instanceof LifecycleProvider){
            return (LifecycleProvider)context;
        }
        return null;
    }

    protected final boolean closeIO(Closeable closeable){
        if (null!=closeable){
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
                //Do nothing
            }
        }
        return false;
    }

    private final Retrofit mRetrofit=new Retrofit();

    protected final <T> T call(Class<T> cls,  com.merlin.api.Callback...callbacks){
        return call(cls,null,callbacks);
    }

    protected final <T> T call(Class<T> cls, Object dither, com.merlin.api.Callback...callbacks){
        Retrofit retrofit=mRetrofit;
        if (null!=cls){
            return retrofit.call(cls,dither,callbacks);
        }
        return null;
    }

    protected final boolean setStatusBar(Integer id,int position){
        if (position== StatusBar.LEFT|| position== StatusBar.CENTER||position== StatusBar.RIGHT){
            View view=findViewById(R.id.status_root_RL);
            return null!=view&&view instanceof StatusBarLayout &&((StatusBarLayout)view).set(id,position);
        }
        return false;
    }

}
