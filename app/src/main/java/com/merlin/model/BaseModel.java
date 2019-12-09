package com.merlin.model;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.protocol.What;

import java.lang.ref.WeakReference;


public class BaseModel implements View.OnClickListener {
    private final Client mClient;
    private final WeakReference<Context> mContext;

    public BaseModel(Context context){
        context=null!=context?(context instanceof Application?context:context.getApplicationContext()) :null;
        mClient=null!=context&&context instanceof Application?((Application)context).getClient():null;
        mContext=null!=context?new WeakReference<>(context):null;
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

    public final boolean isLogin(){
        return null!=getLoginAccount();
    }

    public final String getLoginAccount(){
        Client client=mClient;
        return null!=client?client.getLoginedAccount():null;
    }

    public final boolean sendMessage(String body, String msgTo,String msgType, Callback...callbacks) {
        Client client=mClient;
        if (null!=client){
            return client.sendMessage(body,msgTo,msgType,callbacks);
        }
        Debug.D(getClass(),"Can't send message while client is NULL."+msgTo);
        Socket.notifyResponse(false, What.WHAT_UNKNOWN,null,callbacks);
        return false;
    }

    public final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }
}
