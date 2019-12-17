package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.merlin.bean.FileBrowserMeta;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.server.Json;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;


public class BaseModel implements View.OnClickListener, Tag {
    private WeakReference<View> mRootView=null;
    private final WeakReference<Context> mContext;


    public  interface OnModelViewClick{
        void onViewClick(View v,int id);
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

    public final void toast(String msg){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&null!=msg){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }
    }

    public final View getRoot(){
        WeakReference<View> reference=mRootView;
        return null!=reference?reference.get():null;
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

    public final boolean getAccountClientMeta(String account, Callback ...callbacks){
        if (null!=account){
            JSONObject jsonObject=new JSONObject();
            putIfNotNull(jsonObject,TAG_ACCOUNT,account);
            return getClientMeta(jsonObject,callbacks);
        }
        Debug.D(getClass(),"Can't get account client meta.account="+account);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,callbacks);
        return false;
    }

    public final boolean getClientMeta(JSONObject jsonObject, Callback ...callbacks){
        Client client=getClient();
        if (null!=client){
            return client.getClientMeta(jsonObject,callbacks);
        }
        Debug.D(getClass(),"Can't get client meta.client="+client);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,callbacks);
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
            return client.sendMessage(body,msgTo,msgType,timeout,callbacks);
        }
        Debug.D(getClass(),"Can't send message while client is NULL."+msgTo);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,callbacks);
        return false;
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
        return null!=runnable&&postDelayed(runnable,0);
    }

    protected final boolean postDelayed(Runnable runnable,int delay){
        View root=getRoot();
        if (null!=root&&null!=runnable){
            return root.postDelayed(runnable,delay);
        }
        return false;
    }

    protected final List<Activity> finishAllActivity(Object...activities){
        Application application=getApplication();
        return null!=application?application.finishAllActivity(activities):null;
    }


    protected final boolean startActivity(Class<? extends Activity> cls){
        return startActivity(cls,null);
    }

    protected final boolean startActivity(Class<? extends Activity> cls, Bundle bundle){
        Context context=getContext();
        if (null!=context&&null!=cls){
            Intent intent=new Intent(context,cls);
            if (null!=bundle){
                intent.putExtras(bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
