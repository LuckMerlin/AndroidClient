package com.merlin.socket;

import com.merlin.debug.Debug;
import com.merlin.oksocket.Heartbeat;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.impl.client.PulseManager;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

public class Socket {
    private IConnectionManager mManager;
    private final String mIp;
    private final int mPort;
    private int mHeartbeat;

    public Socket(String ip, int port){
        mIp=ip;
        mPort=port;
    }

    public final synchronized boolean connect(OnConnectFinish change){
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
        final IConnectionManager manager= OkSocket.open(new ConnectionInfo(ip, port));
        if (null!=manager){
            manager.registerReceiver(new ISocketActionListener(){
                @Override
                public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                    Debug.D(getClass(),"链接 onPulseSend "+iPulseSendable);
                    manager.getPulseManager().feed();
                }

                @Override
                public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(),"链接 onSocketConnectionFailed "+s+" "+connectionInfo);
                }

                @Override
                public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {
                       Debug.D(getClass(),"链接 成功 "+s+" "+connectionInfo);
                    manager.getPulseManager().setPulseSendable(new HeartBeater()).pulse();
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(),"链接 onSocketDisconnection "+s+" "+connectionInfo);
                }

                @Override
                public void onSocketIOThreadShutdown(String s, Exception e) {
                    Debug.D(getClass(),"链接 onSocketIOThreadShutdown "+s+" "+e);
                }

                @Override
                public void onSocketIOThreadStart(String s) {
                    Debug.D(getClass(),"链接 onSocketIOThreadStart "+s);
                }

                @Override
                public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
                    Debug.D(getClass(),"链接 onSocketReadResponse "+s);
                }

                @Override
                public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
                    Debug.D(getClass(),"链接 onSocketWriteResponse "+s);
                }
            });
            OkSocketOptions.Builder builder = new OkSocketOptions.Builder(manager.getOption());
            builder.setReaderProtocol(new FrameReader());
            int heartbeat=mHeartbeat;
            builder.setPulseFrequency(heartbeat<=0?3000:heartbeat);
            builder.setPulseFeedLoseTimes(1);
            manager.option(builder.build());
            Debug.D(getClass(),"Start connect socket server.ip="+ip+" port="+port);
            manager.connect();
            mManager=manager;
            return true;
        }
        return false;
    }

    public final boolean isOnline(){
        IConnectionManager manager=mManager;
        return null!=manager&&manager.isConnect();
    }

    public final boolean sendBytes(final byte[] bytes){
        IConnectionManager manager=mManager;
        if (null!=manager){
            manager.send(()->bytes);
            return true;
        }
        Debug.D(getClass(),"Can't send bytes,Not connected."+bytes);
        return false;
    }

    public final boolean isConnected(){
        return null!=mManager;
    }

    public final boolean isConnecting() {
        IConnectionManager manager = mManager;
        return null!=manager&&manager.isDisconnecting();
    }

    public final boolean disconnect(String debug){
        IConnectionManager manager=mManager;
        if (null!=manager){
            Debug.D(getClass(),"Disconnect socket server "+(null!=debug?debug:"."));
            manager.disconnect();
            return true;
        }
        return false;
    }

    public final String getIp() {
        return mIp;
    }

    public final int getPort() {
        return mPort;
    }


}
