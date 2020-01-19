package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.merlin.client.Client;
import com.merlin.client.OnObjectRequestFinish;
import com.merlin.database.DaoMaster;
import com.merlin.database.DaoSession;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.retrofit.Retrofit;
import com.merlin.server.Json;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;


public class BaseModel implements androidx.databinding.DataBindingComponent,View.OnClickListener, View.OnLongClickListener, Tag {
    private WeakReference<View> mRootView=null;
    private final WeakReference<Context> mContext;
    public final static String LABEL_ACTIVITY_DATA = "activityWithData";

    public interface OnIntentChanged{
        void onIntentChange(Intent intent);
    }

    public  interface OnModelViewClick{
        void onViewClick(View v,int id);
    }

    public  interface OnModelViewLongClick{
        boolean onViewLongClick(View v,int id);
    }

    public BaseModel(Context context){
       context=null!=context?(context instanceof Application?context:context.getApplicationContext()) :null;
       mContext=null!=context&&context instanceof Application?new WeakReference<>(context):null;
    }

    protected final Application getApplication(){
        Context context=getContext();
        context=null!=context?context instanceof Application?context:context.getApplicationContext():null;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    protected void onViewAttached(View root){
        //DO nothing
    }

    /**
     * Just for kernel call
     */
    private final void setRootView(View root){
        WeakReference<View> reference=mRootView;
        if (null!=reference){
            reference.clear();
            mRootView=null;
        }
        if (null!=root){
            mRootView=new WeakReference<>(root);
            onViewAttached(root);
        }
    }

    public final void toast(int textResId){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        toast(null!=context?context.getResources().getString(textResId):null);
    }

    public final void toast(String msg){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&null!=msg){
            if (Looper.getMainLooper()==Looper.myLooper()) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }else{
                root.post(()->Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
            }
        }
    }

    public final View getRoot(){
        WeakReference<View> reference=mRootView;
        return null!=reference?reference.get():null;
    }

    public final View findViewById(int id){
        return findViewById(id,View.class);
    }

    public final <T> T findViewById(int id,Class<T> cls){
        View root=getRoot();
        View child=null!=root?root.findViewById(id):null;
        return null!=child&&null!=cls?(T)child:null;
    }

    @Override
    public final void onClick(View v) {
        if (null!=v&&this instanceof  OnModelViewClick) {
            ((OnModelViewClick)this).onViewClick(v,v.getId());
        }
    }

    @Override
    public final boolean onLongClick(View v) {
        if (null!=v&&this instanceof  OnModelViewLongClick) {
            return ((OnModelViewLongClick)this).onViewLongClick(v,v.getId());
        }
        return false;
    }

    public final boolean getAccountClientMeta(String account, Callback ...callbacks){
        if (null!=account){
            JSONObject jsonObject=new JSONObject();
            putIfNotNull(jsonObject,TAG_ACCOUNT,account);
            return getClientMeta(jsonObject,callbacks);
        }
        Debug.D(getClass(),"Can't get account client meta.account="+account);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,"Account is NONE.",callbacks);
        return false;
    }

    public final boolean getClientMeta(JSONObject jsonObject, Callback ...callbacks){
        Client client=getClient();
        if (null!=client){
            return client.getClientMeta(jsonObject,callbacks);
        }
        Debug.D(getClass(),"Can't get client meta.client="+client);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,"Client is None.",callbacks);
        return false;
    }

    public final boolean isLogin(){
        return null!=getLoginAccount();
    }

    public final String getLoginAccount(){
        Client client=getClient();
        return null!=client?client.getLoginedAccount():null;
    }

    public final boolean sendMessage(String body, String msgTo,String msgType,Callback...callbacks) {
        return sendMessage(body,msgTo,msgType,10*1000,callbacks);
    }

    public final boolean sendMessage(String body, String msgTo,String msgType,int timeout, Callback...callbacks) {
        Client client=getClient();
        if (null!=client){
            return client.sendMessage(body,msgTo,msgType,null,timeout,callbacks);
        }
        Debug.D(getClass(),"Can't send message while client is NULL."+msgTo);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,"MsgTo is None.",callbacks);
        return false;
    }

    public final Client.Canceler download(String from, String path, float seek, Socket.OnRequestFinish callback) {
        Client client=getClient();
        if (null!=client){
            return client.download(from,path,seek,callback);
        }
        Debug.W(getClass(),"Can't download file.Client not connected."+path);
        return null;
    }

    protected final DaoSession getDatabaseSession(boolean write){
        DaoMaster.DevOpenHelper helper= getDatabase();
        SQLiteDatabase database= null!=helper?write?helper.getReadableDatabase():helper.getReadableDatabase():null;
        return null!=database?new DaoMaster(database).newSession(IdentityScopeType.Session):null;
    }

    protected final DaoMaster.DevOpenHelper getDatabase(){
        Application application=getApplication();
        return null!=application?application.getDatabase():null;
    }

    protected final Client getClient(){
        Application application=getApplication();
        return null!=application?application.getClient():null;
    }

    public final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    protected final BaseModel putIfNotNull(JSONObject json, String key, Object value){
         Json.putIfNotNull(json,key,value);
         return this;
    }

    protected final <T> T parseObject(String text,Class<T> cls){
        return parseObject(text,cls,null);
    }

    protected final <T> T parseObject(String text,Class<T> cls,T def){
        T data=null!=text&&null!=cls?JSON.parseObject(text, cls):def;
        return null!=data?data:def;
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

    protected final List<Activity> finishAllActivity(Object...activities){
        Application application=getApplication();
        return null!=application?application.finishAllActivity(activities):null;
    }

    protected final boolean startActivity(Class<? extends Activity> cls){
        return startActivityWithBundle(cls,null);
    }

    protected final boolean startActivity(Class<? extends Activity> cls, Serializable data){
        if (null!=data){
            Bundle bundle=new Bundle();
            bundle.putSerializable(LABEL_ACTIVITY_DATA,data);
            return startActivityWithBundle(cls,bundle);
        }
        return startActivityWithBundle(cls,null);
    }

    protected final boolean startActivity(Class<? extends Activity> cls, Parcelable data){
        if (null!=data){
            Bundle bundle=new Bundle();
            bundle.putParcelable(LABEL_ACTIVITY_DATA,data);
            return startActivityWithBundle(cls,bundle);
        }
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

    protected final LayoutInflater getLayoutInflater(){
        Context context=getContext();
        return null!=context?LayoutInflater.from(context):null;
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

    public <T> Client.Canceler request(String from, String url,JSONObject args, OnObjectRequestFinish callback){
        Client client=getClient();
        if (null!=client){
            return client.request(from,url,args,callback);
        }else if (null!=callback){
            callback.onObjectRequested(false,What.WHAT_NONE_LOGIN,"None client",null,null);
        }
        return null;
    }

    protected final <T> T getDataFromIntent(Intent intent,Class<T> cls){
            Bundle bundle=null!=intent?intent.getExtras():null;
            Object data=null!=bundle?bundle.get(LABEL_ACTIVITY_DATA):null;
          return null!=cls&&null!=data&&data.getClass().isAssignableFrom(cls)?(T)data:null;
    }

    private final Retrofit mRetrofit=new Retrofit();

    protected final <T> T call(Class<T> cls, com.merlin.api.Callback...callbacks){
        Retrofit retrofit=mRetrofit;
        if (null!=cls){
            return retrofit.call(cls,callbacks);
        }
        return null;
    }

}
