package com.merlin.client;

import com.merlin.debug.Debug;
import com.merlin.oksocket.LMSocket;
import com.merlin.server.Frame;

import org.json.JSONObject;


public final class Client extends LMSocket {
    private String mAccount=null;

    public Client(String ip, int port){
        super(ip,port);
    }

    public boolean login(String account,String password,Callback ...callbacks){
        if (null==account||account.length()<=0){
            Debug.W(getClass(),"Can't login without account.");
            notifyResponse(false,Callback.REQUEST_FAILED_ARG_INVALID,null,callbacks);
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
        return sendBytes(bytes,TAG_LOGIN,callbacks);
    }

    public boolean logout(){
        return false;
    }

}
