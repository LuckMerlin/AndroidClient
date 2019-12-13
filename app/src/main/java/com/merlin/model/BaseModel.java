package com.merlin.model;

import android.content.Context;
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


public class BaseModel implements View.OnClickListener, Tag {
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Client mClient;
    private final WeakReference<Context> mContext;

    public BaseModel(Context context){
        mContext=null!=context?new WeakReference<>(context):null;
        context=null!=context?(context instanceof Application?context:context.getApplicationContext()) :null;
        mClient=null!=context&&context instanceof Application?((Application)context).getClient():null;
    }

    public final void toast(String msg){
        Context context=getContext();
        if (null!=context&&null!=msg){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
        }
    }

    public void onViewClick(View v,int id){
        //Do nothing
    }

    @Override
    public final void onClick(View v) {
        if (null!=v) {
            onViewClick(v,v.getId());
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
        Client client=mClient;
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
        Client client=mClient;
        return null!=client?client.getLoginedAccount():null;
    }

    public final boolean sendMessage(String body, String msgTo,String msgType,Callback...callbacks) {
        return sendMessage(body,msgTo,msgType,10*1000,callbacks);
    }

    public final boolean sendMessage(String body, String msgTo,String msgType,int timeout, Callback...callbacks) {
        Client client=mClient;
        if (null!=client){
            return client.sendMessage(body,msgTo,msgType,timeout,callbacks);
        }
        Debug.D(getClass(),"Can't send message while client is NULL."+msgTo);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,callbacks);
        return false;
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
        Handler handler=mHandler;
        if (null!=handler&&null!=runnable){
            return handler.postDelayed(runnable,delay);
        }
        return false;
    }
}
