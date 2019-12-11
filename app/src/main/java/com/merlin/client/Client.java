package com.merlin.client;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.server.Frame;

import org.json.JSONObject;

import java.util.Map;

import static com.merlin.server.Json.putIfNotNull;

public final class Client extends Socket {
    private String mAccount=null;

    public Client(String ip, int port){
        super(ip,port);
    }

    public boolean login(String account,String password){
        if (null==account||account.length()<=0){
            Debug.W(getClass(),"Can't login without account.");
            notifyResponse(false, Callback.REQUEST_FAILED_ARG_INVALID,null,null);
            return true;
        }
        JSONObject json=new JSONObject();
        putIfNotNull(json,TAG_ACCOUNT,account);
        putIfNotNull(json,TAG_PASSWORD,password);
        JSONObject meta=new JSONObject();
        putIfNotNull(meta,TAG_DEVICE_TYPE,"Android:"+android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL);
        putIfNotNull(json,TAG_META,meta);
        String data=null!=json?json.toString():null;
        byte[] bytes=null!=data? Frame.encodeString(data):null;
        return sendBytes(bytes, TAG_LOGIN,(OnRequestFinish)(succeed,what,frame)->{
            Debug.D(getClass(),"Login finish."+succeed);
            mAccount = succeed?account:null;
            notifyStatusChanged(false,OnClientStatusChange.LOGIN_IN,account);
        });
    }

    public boolean logout(){
        return false;
    }

    public boolean getClientMeta(com.alibaba.fastjson.JSONObject jsonObject, Callback ...callbacks){
        String body=null!=jsonObject?jsonObject.toJSONString():null;
        return sendMessage(body,null,TAG_GET_CLIENTS,callbacks);
    }

    public boolean isLogined(){
        return null!=mAccount;
    }

    public String getLoginedAccount(){
        return mAccount;
    }

}
