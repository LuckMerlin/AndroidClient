package com.merlin.client;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Callback;
import com.merlin.oksocket.Socket;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.protocol.What;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.util.Closer;
import com.merlin.util.FileMaker;

import org.json.JSONObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static com.merlin.server.Json.putIfNotNull;

public final class Client extends Socket {
    private String mAccount=null;

    public interface OnFileDownloadUpdate{
        int DOWNLOAD_SUCCEED=4000;
        int DOWNLOAD_ARGS_INVALID=4001;
        int DOWNLOAD_CREATE_FAIL=4002;
        int DOWNLOAD_EXCEPTION=4003;
        int DOWNLOAD_WRITE_EXCEPTION=4004;
        int DOWNLOAD_WRITE=4005;
        void onFileDownloadUpdate(boolean finish, int what, String account,String url, String to,byte[] data);
    }

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

    public Canceler downloadFile(String from,String path,String to,OnFileDownloadUpdate callback){
        if (null==to||null==path||to.length()<=0||path.length()<=0){
            notifyFileDownloadUpdate(true,OnFileDownloadUpdate.DOWNLOAD_ARGS_INVALID,from,path,to,null,callback);
            return null;
        }
        final File file=new FileMaker().makeFile(to);
        if (null!=file&&file.isFile()){
            FileOutputStream fos=null;
            final Closer closer=new Closer();
            Canceler canceler=null;
            try {
                fos=new FileOutputStream(file);
                final FileOutputStream os=fos;
                Debug.D(getClass(),"Downloading file from "+from+" \n"+path+" to "+to);
                return canceler=download(from,path,0,(succeed,what,note,frame)->{
                    if (succeed){
                        if (null!=frame){
                            byte[] bytes=frame.getBodyBytes();
                            int length=null!=bytes?bytes.length:-1;
                            try {
                                os.write(bytes,0,length);
                                notifyFileDownloadUpdate(false,OnFileDownloadUpdate.DOWNLOAD_WRITE,from,path,to,bytes,callback);
                                if (frame.isLastFrame()){
                                    Debug.D(getClass(),"最后一个 ");
                                    os.flush();
                                    closer.close(os);
                                    notifyFileDownloadUpdate(true,OnFileDownloadUpdate.DOWNLOAD_SUCCEED,from,path,to,bytes,callback);
                                }
                            }catch (Exception e){
                                Debug.E(getClass(),"Failed download file.e="+e,e);
                                closer.close(os);
                                notifyFileDownloadUpdate(true,OnFileDownloadUpdate.DOWNLOAD_WRITE_EXCEPTION,from,path,to,bytes,callback);
                            }
                        }
                    }else{
                        Debug.W(getClass(),"Failed download file."+what+" "+note);
                        notifyFileDownloadUpdate(true,what,from,path,to,null,callback);
                    }
                });
            } catch (FileNotFoundException e) {
                Debug.E(getClass(),"Download file exception.e="+e+" "+to,e);
                notifyFileDownloadUpdate(true,OnFileDownloadUpdate.DOWNLOAD_EXCEPTION,from,path,to,null,callback);
               return null;
            }finally {
                if (null==canceler){
                    closer.close(fos);
                }
            }
        }
        notifyFileDownloadUpdate(true,OnFileDownloadUpdate.DOWNLOAD_CREATE_FAIL,from,path,to,null,callback);
        return null;
    }

    public Canceler download(String from,String path,double seek,OnRequestFinish callback) {
        final int timeout=30*1000;
        return download(from,path,seek,timeout,callback);
    }

    public Canceler download(String from,String path,double seek,int timeout,OnRequestFinish callback){
        if (null==path||null==callback||null==from){
            Debug.W(getClass(),"Can't download file with client."+from+" "+path+" "+callback);
            return null;
        }
        JSONObject object=new JSONObject();
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_READ_FILE);
        Json.putIfNotNull(object,TAG_POSITION,seek);
        Json.putIfNotNull(object,TAG_FILE,path);
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

    public <T> Canceler request(String from,String url,JSONObject args,OnObjectRequestFinish callback){
        if (null==url||null==callback||null==from){
            Debug.W(getClass(),"Can't request object with client."+from+" "+url+" "+callback);
            if (null!=callback){
                callback.onObjectRequested(false, What.WHAT_ARGS_INVALID,"Args invalid.",null,null);
            }
            return null;
        }
        JSONObject object=null!=args?args:new JSONObject();
        Json.putIfNotNull(object,TAG_ACCOUNT,from);
        Json.putIfNotNull(object,TAG_URL,url);
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_REQUEST);
        final Canceler cancel=new Canceler();
        return sendMessage(object.toString(), from, TAG_MESSAGE_QUERY, null, -1,(OnRequestFinish)(succeed,what,note,frame)->{
                    if (succeed){
                        T data=null;
                        if (frame.isExistBody()) {
                            Type[] types = callback.getClass().getGenericInterfaces();
                            Type type = null != types && types.length >= 1 ? types[0] : null;
                            ParameterizedType pt = null != type && type instanceof ParameterizedType ? (ParameterizedType) type : null;
                            types = null != pt ? pt.getActualTypeArguments() : null;
                            type = null != types && types.length >= 1 ? types[0] : null;
                            if (null != type) {
                                if (type instanceof Class) {
                                    data = frame.getBody((Class<T>) type, null);
                                } else if (type instanceof ParameterizedType) {
                                    Type[] actTypes=((ParameterizedType) type).getActualTypeArguments();
                                    Type actType=null!=actTypes&&actTypes.length>=1?actTypes[0]:null;
                                    type = null!=actType?((ParameterizedType) type).getRawType():null;
                                    if (null != type && type.equals(List.class)) {
                                        data = (T) frame.getBodyArray((Class) actType, null);
                                    }
                                }
                            }
                        }
                        callback.onObjectRequested(true,what,note,frame,data);
                    }else{
                        callback.onObjectRequested(false,what,note,frame,null);
                    }
                })?cancel:null;
    }

    private void notifyFileDownloadUpdate(boolean finish,int what,String account,String from,String to,byte[] data,OnFileDownloadUpdate callback){
            if (null!=callback){
                callback.onFileDownloadUpdate(finish,what,account,from,to,data);
            }
    }


}
