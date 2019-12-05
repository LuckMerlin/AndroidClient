package com.merlin.oksocket;

import com.merlin.debug.Debug;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

public final class LMSocket {
   private Listener mListener;
   private int mHeartbeat = 0;
   private final String mIp;
   private final int mPort;

   public LMSocket(String ip, int port){
       mIp=ip;
       mPort=port;
   }

   public boolean connect(FrameParser parser){
       IConnectionManager manager=getManager();
       if (null!=manager){
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
       manager= OkSocket.open(new ConnectionInfo(ip, port));
       if (null!=manager){
           manager.registerReceiver(mListener=new Listener(manager,parser));
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

   public boolean isOnline(){
       IConnectionManager manager=getManager();
       return null!=manager&&manager.isConnect();
   }

   public boolean isConnected(){
       return null!=getManager();
   }

   public boolean disconnect(String debug){
       IConnectionManager manager=getManager();
       Listener listener=mListener;
       mListener=null;
       if (null!=manager){
           Debug.D(getClass(),"Disconnect server "+(null!=debug?debug:"."));
           if (null!=listener){
               manager.unRegisterReceiver(listener);
           }
           manager.disconnect();
           return true;
       }
       return false;
   }

   public boolean isConnecting() {
        IConnectionManager manager = getManager();
        return null!=manager&&manager.isDisconnecting();
    }

   public String getmIp() {
        return mIp;
    }

   public int getPort() {
        return mPort;
    }

   private IConnectionManager getManager(){
       Listener listener=mListener;
       return null!=listener?listener.mManager:null;
   }

   private static class  Listener extends  SocketActionAdapter{
        private IConnectionManager mManager;
        private FrameParser mFrameParser;

        private Listener(IConnectionManager manager,FrameParser parser){
            mManager=manager;
            mFrameParser=parser;
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            Debug.D(getClass(), "连接成功");
            mManager.getPulseManager().setPulseSendable(new Heartbeat()).pulse();
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            super.onSocketConnectionFailed(info, action, e);
            Debug.D(getClass(), "onSocketConnectionFailed");
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            super.onSocketDisconnection(info, action, e);
            Debug.D(getClass(), "onSocketDisconnection");

        }

        @Override
        public void onSocketIOThreadShutdown(String action, Exception e) {
            super.onSocketIOThreadShutdown(action, e);
            Debug.D(getClass(), "onSocketIOThreadShutdown");
        }

        @Override
        public void onSocketIOThreadStart(String action) {
            super.onSocketIOThreadStart(action);
            Debug.D(getClass(), "onSocketIOThreadStart");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            FrameParser parser = null != data ? mFrameParser : null;
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
            super.onPulseSend(info, data);
            mManager.getPulseManager().feed();
        }
    };

}
