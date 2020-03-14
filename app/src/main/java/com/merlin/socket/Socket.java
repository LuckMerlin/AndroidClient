package com.merlin.socket;

import com.merlin.bean.Love;
import com.merlin.debug.Debug;
import com.merlin.util.Byte;
import com.merlin.util.Int;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.util.UUID;

public class Socket {
    private IConnectionManager mManager;
    private final String mIp;
    private final int mPort;
    private int mHeartbeat;

    public Socket(String ip, int port){
        mIp=ip;
        mPort=port;
    }

    private final OnFrameReceive mFrameReceive=new OnFrameReceive() {
        @Override
        public void OnFrameReceived(Frame frame) {
            Debug.D(getClass(),"收到针 "+frame.isTerminal() +" "+frame.getKey()+" "+frame.getBodyText(null));
        }
    };

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
            final FrameReader frameReader=new FrameReader();
            manager.registerReceiver(new ISocketActionListener(){
                @Override
                public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                    manager.getPulseManager().feed();
//                    Debug.D(getClass(),"发送心跳 "+manager.getPulseManager().getPulseSendable());
                }

                @Override
                public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(),"Fail connect socket connect."+ip+" "+port+" "+s);
                }

                @Override
                public void onSocketConnectionSuccess(ConnectionInfo ci, String s) {
                    Debug.D(getClass(),"Succeed connect socket."+ip+" "+port+" "+s);
                    manager.getPulseManager().setPulseSendable(new HeartBeater()).pulse();
                    if (null==mManager){
                        mManager=manager;
                    }
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(),"Disconnected socket."+ip+" "+port+" "+s);
                }

                @Override
                public void onSocketIOThreadShutdown(String s, Exception e) {
                    IConnectionManager curr=mManager;
                    if(null!=curr&&curr==manager&&!manager.isConnect()){
                        mManager=null;
                    }
                }

                @Override
                public void onSocketIOThreadStart(String s) {
//                    Debug.D(getClass(),"链接 onSocketIOThreadStart "+s);
                }

                @Override
                public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
                    if (null!=originalData){
                        Integer frameHeadLength = Int.toInt(originalData.getHeadBytes(), 0, null);
                        byte[] bodyBytes=originalData.getBodyBytes();
                        Frame.read(bodyBytes,frameHeadLength,mFrameReceive);
                    }
                }

                @Override
                public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
//                    Debug.D(getClass(),"onSocketWriteResponse "+ iSendable.getClass());
                }
            });
            OkSocketOptions.Builder builder = new OkSocketOptions.Builder(manager.getOption());
            builder.setReaderProtocol(frameReader);
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

    public final boolean sendText(String text,String toAccount,String debug){
        try {
            final String encoding="utf-8";
            byte[] bodyBytes=null!=text&&text.length()>0?text.getBytes(encoding):null;
            int bodyBytesLength=null!=bodyBytes?bodyBytes.length:-1;
            if (bodyBytesLength<=0){
                Debug.E(getClass(),"Can't send text to "+toAccount+" which body bytes invalid "+(null!=debug?debug:".")+" "+bodyBytesLength);
                return false;
            }
            String unique=generateUnique(Frame.FORMAT_TEXT);
            Frame frame=new Frame(true,null,toAccount,Frame.FORMAT_TEXT,unique,null,bodyBytes,null,null,encoding);
            byte[] frameBytes=null!=frame?frame.toFrameBytes():null;
            int frameBytesLength=null!=frameBytes?frameBytes.length:-1;
            if (frameBytesLength<=0){
                Debug.E(getClass(),"Can't send text to "+toAccount+" which frame bytes invalid "+(null!=debug?debug:".")+" "+frameBytesLength);
                return false;
            }
            return sendBytes(frameBytes);
        } catch (Exception e) {
            Debug.E(getClass(),"Exception send text to "+toAccount+" "+(null!=debug?debug:".")+e,e);
            e.printStackTrace();
            return false;
        }
    }

    protected final boolean sendFrame(Frame frame,String debug){
        byte[] bytes=null!=frame?frame.toFrameBytes():null;
        int length=null!=bytes?bytes.length:-1;
        if (length>0){
            return sendBytes(bytes);
        }
        Debug.W(getClass(),"Can't send frame with invalid bytes "+(null!=debug?debug:".")+" length="+length);
        return false;
    }

    private final boolean sendBytes(final byte[] bytes){
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
            Debug.D(getClass(),"Disconnecting socket server "+(null!=debug?debug:"."));
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

    public final String generateUnique(String prefix){
        return "Android"+(null!=prefix?prefix:"")+System.currentTimeMillis()+UUID.randomUUID();
    }

}
