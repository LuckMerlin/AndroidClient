package com.merlin.client;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.server.Frame;
import com.merlin.server.Json;

import org.json.JSONObject;


import static com.merlin.server.Json.putIfNotNull;

public final class Client extends Socket {
    private String mAccount=null;

    public static final class Canceler{
        private boolean mCanceled=false;

        public boolean cancel(boolean cancel){
            if (cancel!=mCanceled){
                mCanceled=cancel;
                return true;
            }
            return false;
        }

        public boolean isCanceled() {
            return mCanceled;
        }
    }

    public Client(String ip, int port){
        super(ip,port);
    }

    public boolean login(String account,String password){
        if (null==account||account.length()<=0){
            Debug.W(getClass(),"Can't login without account.");
            notifyResponse(false, Callback.REQUEST_FAILED_ARG_INVALID,null,"None account.",null);
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
        return sendBytes(bytes, TAG_LOGIN,(OnRequestFinish)(succeed,what,note,frame)->{
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

    public Canceler download(String from,String path,float seek,OnRequestFinish callback){
        if (null==path||null==callback||null==from){
            Debug.W(getClass(),"Can't download file with client."+from+" "+path+" "+callback);
            return null;
        }
        JSONObject object=new JSONObject();
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_READ_FILE);
        Json.putIfNotNull(object,TAG_POSITION,seek);
        Json.putIfNotNull(object,TAG_FILE,path);
        final int timeout=30*1000;
        final Canceler cancel=new Canceler();
        return sendMessage(object.toString(), from, TAG_MESSAGE_QUERY, null, timeout, new OnRequestFinish() {
                    @Override
                    public void onRequestFinish(boolean succeed, int what, String note, Frame frame) {
                        callback.onRequestFinish(succeed,what,note,frame);
                        if (succeed&&null!=frame&&!frame.isLastFrame()) { //Trigger next frame
                            String msgFrom= frame.getMsgFrom();
                            String unique=frame.getUnique();
                            sendMessage(cancel.mCanceled?TAG_CANCEL:TAG_MESSAGE_NEXT_FRAME,msgFrom,TAG_MESSAGE_NEXT_FRAME,unique,timeout,this);
                        }
                    }
                }

        )?cancel:null;
    }

}
