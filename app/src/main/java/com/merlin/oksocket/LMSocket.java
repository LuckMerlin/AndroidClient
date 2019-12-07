package com.merlin.oksocket;

import android.os.Handler;

import com.merlin.client.Client;
import com.merlin.protocol.Protocol;
import com.merlin.protocol.Tag;
import com.merlin.debug.Debug;
import com.merlin.server.Frame;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LMSocket implements Tag {
   private final Handler mHandler=new Handler();
   private final Map<String,RequestingRunnable> mRequesting=new HashMap<>();
   private OnFrameReceive mReceiveListener;
   private OnClientStatusChange mStatusChange;
   private IConnectionManager mManager;
   private int mHeartbeat = 0;
   private final String mIp;
   private final int mPort;
   private int mTimeout=10000;

   public interface OnSocketConnectListener{
       int CONNECT_SUCCEED =1110;
       void onSocketConnectChanged(boolean connected,int what);
   }

   public interface Callback{
      int REQUEST_SUCCEED=10000;
      int REQUEST_FAILED_SEND_FAIL=10001;
      int REQUEST_FAILED_ARG_INVALID=10002;
      int REQUEST_FAILED_TIMEOUT=10003;
   }

   public interface SynchronizationCallback{

   }

   public interface OnRequestFinish extends Callback{
        void onRequestFinish(boolean succeed,int what,Frame frame);
   }

   public LMSocket(String ip, int port){
       mIp=ip;
       mPort=port;
   }

   public void setOnClientStatusChange(OnClientStatusChange change){
        mStatusChange=change;
   }

   public void setOnFrameReceive(OnFrameReceive listener){
       mReceiveListener=listener;
   }

   public final boolean connect(OnSocketConnectListener connectListener){
       IConnectionManager currManager=mManager;
       if (null!=currManager){
           Debug.D(getClass(),"Not need connect again while connected.");
           return false;
       }
       final String ip=mIp;
       final int port =mPort;
       if (null==ip||port<=0){
           Debug.W(getClass(),"Can't connect server.Invalid address?ip="+ip+" port="+port);
           return false;
       }
       Debug.D(getClass(),"Connect server.ip="+ip+" port="+port);
       final IConnectionManager manager= OkSocket.open(new ConnectionInfo(ip, port));
       if (null!=manager){
           final FrameParser.OnFrameParseListener listener=(frame)->{
               String unique=null!=frame?frame.getUnique():null;
               Map<String,RequestingRunnable> requesting=null!=unique&&unique.length()>0?mRequesting:null;
               RequestingRunnable runnable=null!=requesting?requesting.get(unique):null;
               if (null!=runnable){
                   removeResponseWaiting(unique,"While request responsed.");
                   runnable.onResponse(frame);
               }
               OnFrameReceive receiveListener=mReceiveListener;
               if (null!=receiveListener){
                   receiveListener.onFrameReceived(frame,(Client)this);
               }};
           final FrameParser parser=new FrameParser(listener);
           ISocketActionListener innerListener=new ISocketActionListener(){
               @Override
               public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                   Debug.D(getClass(), "连接成功");
                   mManager=manager;
                   manager.getPulseManager().setPulseSendable(new Heartbeat()).pulse();
                   if (null!=connectListener){
                       connectListener.onSocketConnectChanged(true,OnSocketConnectListener.CONNECT_SUCCEED);
                   }
               }

               @Override
               public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
                   Debug.D(getClass(), "onSocketConnectionFailed");
               }

               @Override
               public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
                   Debug.D(getClass(), "onSocketDisconnection");
                   mManager=null;
               }

               @Override
               public void onSocketIOThreadShutdown(String action, Exception e) {
                   Debug.D(getClass(), "onSocketIOThreadShutdown");
                   mManager=null;
               }

               @Override
               public void onSocketIOThreadStart(String action) {
                   Debug.D(getClass(), "onSocketIOThreadStart");
               }

               @Override
               public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                   if (null != parser) {
                       parser.onFrameBytesReceived(data.getHeadBytes(), data.getBodyBytes());
                   }
               }

               @Override
               public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
                   Debug.D(getClass(), "onSocketWriteResponse");
               }

               @Override
               public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
                   mManager.getPulseManager().feed();
               }
           };
           manager.registerReceiver(innerListener);
           OkSocketOptions.Builder builder = new OkSocketOptions.Builder(manager.getOption());
           builder.setReaderProtocol(new FrameReader());
           int heartbeat=mHeartbeat;
           builder.setPulseFrequency(heartbeat<=0?3000:heartbeat);
           builder.setPulseFeedLoseTimes(1);
           manager.option(builder.build());
           manager.connect();
           Debug.D(getClass(),"Connected server.ip="+ip+" port="+port);
       }
       return null!=manager;
   }

   public final boolean isOnline(){
       IConnectionManager manager=mManager;
       return null!=manager&&manager.isConnect();
   }

   public final boolean isConnected(){
       return null!=mManager;
   }

   public final boolean disconnect(String debug){
       IConnectionManager manager=mManager;
       if (null!=manager){
           Debug.D(getClass(),"Disconnect server "+(null!=debug?debug:"."));
           manager.disconnect();
           return true;
       }
       return false;
   }

   public final boolean isConnecting() {
        IConnectionManager manager = mManager;
        return null!=manager&&manager.isDisconnecting();
    }

   public final String getmIp() {
        return mIp;
    }

   public final int getPort() {
        return mPort;
    }

   protected final boolean sendBytes(final byte[] bytes){
       IConnectionManager manager=mManager;
       if (null!=manager){
           return null!=manager.send(()->bytes);
       }
       return false;
   }

   public final boolean sendMessage(String body,Callback...callbacks) {
        byte[] bodyBytes=null!=body&&body.length()>0?Frame.encodeString(body,Protocol.ENCODING):null;
        if (null==bodyBytes||bodyBytes.length<=0){
            notifyResponse(false,Callback.REQUEST_FAILED_SEND_FAIL,null,callbacks);
            return false;
        }
        return sendBytes(bodyBytes,TAG_FRAME_TEXT_MESSAGE,null,null,mTimeout,callbacks);
    }

   public final boolean sendText(String body,Callback...callbacks) {
        byte[] bodyBytes=null!=body&&body.length()>0?Frame.encodeString(body,Protocol.ENCODING):null;
        if (null==bodyBytes||bodyBytes.length<=0){
            notifyResponse(false,Callback.REQUEST_FAILED_SEND_FAIL,null,callbacks);
            return false;
        }
        return sendBytes(bodyBytes,TAG_FRAME_TEXT_DATA,null,null,mTimeout,callbacks);
    }

   public final boolean sendBytes(byte[] body, String type, Callback...callbacks) {
        return sendBytes(body,type,null,null,mTimeout,callbacks);
    }

    public final boolean sendBytesTo(byte[] body,String msgTo,Callback...callbacks) {
           return sendBytes(body,TAG_FRAME_TEXT_DATA,msgTo,null,mTimeout,callbacks);
    }

    public final boolean sendBytes(byte[] body, String type, String msgTo, String uniqueValue,int timeout, Callback...callbacks){
        if (null==type||type.length()<=0){
            Debug.E(getClass(),"Can't send bytes,NONE type defined.");
            notifyResponse(false,Callback.REQUEST_FAILED_ARG_INVALID,null,callbacks);
            return false;
        }
        long timestamp=System.currentTimeMillis();
        final String unique=null!=uniqueValue&&uniqueValue.length()>0?uniqueValue:Long.toString(timestamp)+hashCode()+(Math.random()*Integer.MAX_VALUE);
        JSONObject head=new JSONObject();
        putIfNotNull(head,TAG_TO,msgTo);
        putIfNotNull(head,TAG_TYPE,type);
        putIfNotNull(head,TAG_UNIQUE,unique);
        putIfNotNull(head,TAG_VERSION, Protocol.PROTOCOL_VERSION);
        putIfNotNull(head,TAG_TIMESTAMP,timestamp);
        putIfNotNull(head,TAG_SECRET_KEY,null);
        String string=null!=head&&head.length()>0?head.toString():null;
        byte[] headBytes= Frame.encodeString(string,Protocol.ENCODING);
        byte[] bytes=Protocol.generateFrame(headBytes,body);
        timeout=timeout<=0?mTimeout:timeout;
        timeout=timeout<=0?5000:timeout;
        final RequestingRunnable runnable=new RequestingRunnable(unique,timeout,callbacks){
            @Override
            public void run() {
                notifyResponse(false,Callback.REQUEST_FAILED_TIMEOUT,null,callbacks);
            }

            @Override
            void onResponse(Frame frame) {
                notifyResponse(true,Callback.REQUEST_SUCCEED,frame,callbacks);
            }
        };
        mRequesting.put(unique,runnable);
        mHandler.postDelayed(runnable,timeout<=0?5000:timeout);
        if (null!=bytes&&bytes.length>0&&sendBytes(bytes)){
            return true;
        }
        removeResponseWaiting(unique,"While request send failed.");
        notifyResponse(false,Callback.REQUEST_FAILED_SEND_FAIL,null,callbacks);
       return false;
    }

   public final boolean putIfNotNull(JSONObject json,String key,Object value){
        if (null!=json&&null!=key&&null!=value){
            try {
                json.put(key,value);
            } catch (JSONException e) {
                Debug.E(getClass(),"Can't put string into json.e="+e+" key="+key+" value="+value,e);
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

   private boolean removeResponseWaiting(String unique,String debug){
       Map<String,RequestingRunnable> requesting=mRequesting;
       Handler handler=mHandler;
       RequestingRunnable runnable= null!=unique&&null!=requesting?requesting.remove(unique):null;
       if (null!=runnable){
           if (null!=handler){
               handler.removeCallbacks(runnable);
           }
           Debug.D(getClass(),"Remove response waiting "+(null!=debug?debug:".")+" "+unique);
           return true;
       }
       return false;
    }

   protected final void notifyResponse(boolean succeed,int what,Frame frame,Callback[] callbacks){
        if (null!=callbacks&&callbacks.length>0){
            for (Callback callback:callbacks) {
                 if (null==callback){
                     continue;
                 }
                 if (callback instanceof OnRequestFinish){
                     if ((callback instanceof SynchronizationCallback)){
                         ((OnRequestFinish)callback).onRequestFinish(succeed,what,frame);
                     }else{
                         mHandler.post(()->((OnRequestFinish)callback).onRequestFinish(succeed,what,frame));
                     }

                 }
            }
        }
    }

    protected final void notifyStatusChanged(boolean autoNotify,int what,Object data){
       OnClientStatusChange change=mStatusChange;
       if (null!=change){
           change.onClientStatusChanged(autoNotify,what,data,(Client)this);
       }
    }

   private static abstract class RequestingRunnable implements Runnable{
       private final Callback[]  mCallbacks;
       private final int mTimeout;
       private final String mUnique;

        private RequestingRunnable(String unique,int timeout,Callback ...callbacks){
            mCallbacks=callbacks;
            mUnique=unique;
            mTimeout=timeout;
        }

        abstract void onResponse(Frame frame);

    }

}
