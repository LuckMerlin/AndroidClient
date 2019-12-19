package com.merlin.client;

import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.server.Frame;
import com.merlin.server.Json;

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

    public boolean getClientMeta(JSONObject jsonObject, Callback ...callbacks){
        String body=null!=jsonObject?jsonObject.toString():null;
        byte[] bytes=null!=body? Frame.encodeString(body):null;
        return sendBytes(bytes,TAG_GET_CLIENTS,callbacks);
    }

    public boolean isLogined(){
        return null!=mAccount;
    }

    public String getLoginedAccount(){
        return mAccount;
    }

    public boolean download(String from,String path,OnRequestFinish callback){
        if (null==path||null==callback||null==from){
            Debug.W(getClass(),"Can't download file with client."+from+" "+path+" "+callback);
            return false;
        }
        JSONObject object=new JSONObject();
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_READ_FILE);
        Json.putIfNotNull(object,TAG_FILE,path);
        return sendMessage(object.toString(), from, TAG_MESSAGE_QUERY,30*1000,(OnRequestFinish)(succeed,what,frame)->
                callback.onRequestFinish(succeed,what,frame));
    }

}
