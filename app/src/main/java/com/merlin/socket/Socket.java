package com.merlin.socket;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.merlin.api.JsonObject;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.FileMeta;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.util.Int;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.ISocketActionListener;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Socket {
    private IConnectionManager mManager;
    private final String mIp;
    private final int mPort;
    private int mHeartbeat;
    private int mTimeout;
    private final Map<String, WaitingResponse> mResponseWaiting = new ConcurrentHashMap<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public Socket(String ip, int port) {
        mIp = ip;
        mPort = port;
    }

    private final OnFrameReceive mFrameReceive = (frame)-> {
            if (null != frame) {
                String unique = frame.getUnique();
                Map<String, WaitingResponse> map = null != unique ? mResponseWaiting : null;
                boolean terminal=frame.isTerminal();
                WaitingResponse waiting = null != map ? map.remove(unique) : null;
                if (null != waiting) {
                    Handler handler = mHandler;
                    handler.removeCallbacks(waiting);
                    OnResponse onResponse = waiting.mOnResponse;
                    final boolean canceled=waiting.isCancel();
                    Integer next=null!=onResponse?onResponse.onResponse(canceled?What.WHAT_CANCEL:What.WHAT_SUCCEED,
                            "Response succeed"+(canceled?" But canceled.":"."), waiting.mFrame, frame, null):null;
                    if (!canceled&&!terminal&&null!=next&&next==OnResponse.NEXT_FRAME){//If need request next frame
                        Frame nextFrameCall=null!=waiting?waiting.mFrame:null;
                        if (null!=nextFrameCall){
                            nextFrameCall.setPosition(frame.getPosition()+1);//Increase 1 to start from next byte
                            sendFrame(nextFrameCall,waiting.mOnResponse,"After pre frame handled.");
                        }else{
                            Debug.W(getClass(),"Can't call next frame which next frame call is NULL.");
                        }
                    }
                }
            }
    };

    public final synchronized boolean connect(OnConnectFinish change,String debug) {
        IConnectionManager currManager = mManager;
        if (null != currManager) {
            Debug.D(getClass(), "Not need connect again while connected.");
            return false;
        }
        final String ip = mIp;
        final int port = mPort;
        if (null == ip || port <= 0) {
            Debug.W(getClass(), "Can't connect server.Invalid address?ip=" + ip + " port=" + port);
            return false;
        }
        final IConnectionManager manager = OkSocket.open(new ConnectionInfo(ip, port));
        if (null != manager) {
            final FrameReader frameReader = new FrameReader();
            manager.registerReceiver(new ISocketActionListener() {
                @Override
                public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                    manager.getPulseManager().feed();
//                    Debug.D(getClass(),"发送心跳 "+manager.getPulseManager().getPulseSendable());
                }

                @Override
                public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(), "Fail connect socket connect." + ip + " " + port + " " + s);
                }

                @Override
                public void onSocketConnectionSuccess(ConnectionInfo ci, String s) {
                    Debug.D(getClass(), "Succeed connect socket." + ip + " " + port + " " + s);
                    manager.getPulseManager().setPulseSendable(new HeartBeater()).pulse();
                    if (null == mManager) {
                        mManager = manager;
                    }
                }

                @Override
                public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                    Debug.D(getClass(), "Disconnected socket." + ip + " " + port + " " + s);
                }

                @Override
                public void onSocketIOThreadShutdown(String s, Exception e) {
                    IConnectionManager curr = mManager;
                    if (null != curr && curr == manager && !manager.isConnect()) {
                        mManager = null;
                    }
                }

                @Override
                public void onSocketIOThreadStart(String s) {
//                    Debug.D(getClass(),"链接 onSocketIOThreadStart "+s);
                }

                @Override
                public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
                    if (null != originalData) {
                        Integer frameHeadLength = Int.toInt(originalData.getHeadBytes(), 0, null);
                        byte[] bodyBytes = originalData.getBodyBytes();
                        Frame.read(bodyBytes, frameHeadLength, mFrameReceive);
                    }
                }

                @Override
                public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {
//                    Debug.D(getClass(),"onSocketWriteResponse "+ iSendable.getClass());
                }
            });
            OkSocketOptions.Builder builder = new OkSocketOptions.Builder(manager.getOption());
            builder.setReaderProtocol(frameReader);
            int heartbeat = mHeartbeat;
            builder.setPulseFrequency(heartbeat <= 0 ? 3000 : heartbeat);
            builder.setPulseFeedLoseTimes(1);
            manager.option(builder.build());
            Debug.D(getClass(), "Start connect socket server.ip=" + ip + " port=" + port);
            manager.connect();
            mManager = manager;
            return true;
        }
        return false;
    }

    public final boolean isOnline() {
        IConnectionManager manager = mManager;
        return null != manager && manager.isConnect();
    }

    public final Canceler downloadFile(String path,double position,String toAccount,OnResponse callback,String debug){
        if (null!=path&&path.length()>0){
            String dataText=new JsonObject().put(Label.LABEL_MODE,Label.LABEL_DOWNLOAD).put(Label.LABEL_PATH,path).toString();
            Frame frame=createTextFrame(dataText,toAccount,generateUnique("DownloadFileToAndroid"),debug);
            if (null==frame){
                Debug.W(getClass(),"Can't download file which create frame failed "+(null!=debug?debug:"."));
                return null;
            }
            frame.setPosition(position);
            return sendFrame(frame,callback,debug);
        }
        Debug.W(getClass(),"Can't download file which path is invalid "+(null!=debug?debug:"."));
        return null;
    }

    public final Canceler uploadFile(File file,String toAccount,String folder,String name,OnResponse callback,String debug){
        if (null!=file&&null!=name&&name.length()>0){
            if (!file.exists()){
                Debug.W(getClass(),"Can't upload file which file not exist "+(null!=debug?debug:".")+" "+file);
                return null;
            }
            if(!file.canRead()){
                Debug.W(getClass(),"Can't upload file which file none permission "+(null!=debug?debug:"."));
                return null;
            }
            final boolean isDirectory=file.isDirectory();
            JsonObject json=new JsonObject().put(Label.LABEL_MODE,Label.LABEL_UPLOAD).put(Label.LABEL_PARENT,folder).put(Label.LABEL_NAME,name);
            json=isDirectory?json.put(Label.LABEL_FOLDER, Label.LABEL_FOLDER):json;
            String dataText=null!=json?json.toString():null;
            Frame frame=createTextFrame(dataText,toAccount,generateUnique("UploadFileFromAndroid"),debug);
            if (null==frame){
                Debug.W(getClass(),"Can't upload file which create frame failed "+(null!=debug?debug:"."));
                return null;
            }
            return sendFrame(frame,new FileUploader(file,frame,callback){
                @Override
                protected Canceler onFrameSend(Frame frame,String debug) {
                    return sendFrame(frame,this,debug);
                }
            },debug);
        }
        Debug.W(getClass(),"Can't upload file which path or name is invalid "+(null!=debug?debug:"."));
        return null;
    }

    public final Canceler sendText(String text, String toAccount, OnResponse callback, String debug) {
        return sendText(text,toAccount,null,callback,debug);
    }

    public final Canceler sendText(String text, String toAccount,String unique, OnResponse callback, String debug){
        int length=null!=text?text.length():0;
        if (length<=0){
            return null;
        }
        Frame frame=createTextFrame(text,toAccount,null!=unique&&unique.length()>0?unique:generateUnique("TextFromAndroid"+System.identityHashCode(text)+
                System.identityHashCode(callback)),debug);
        if (null==frame){
            Debug.E(getClass(),"Can't send text to "+toAccount+" which frame invalid "+(null!=debug?debug:".")+" ");
            return null;
        }
        return sendFrame(frame,callback,debug);
    }

    protected final Canceler sendFrame(Frame frame, OnResponse callback, String debug){
            byte[] bytes=null!=frame?frame.toFrameBytes():null;
            int length=null!=bytes?bytes.length:-1;
            if (length>0){
                final String unique=frame.getUnique();
                final Map<String,WaitingResponse> waiting=mResponseWaiting;
                final Handler handler=mHandler;
                WaitingResponse waitingResponse=null;
                if (null!=callback){//Save callback for response
                    if (null==waiting){
                        Debug.W(getClass(),"Will not receive frame response while waiting queue NULL.");
                    }else if (null==unique||unique.length()<=0){
                        Debug.W(getClass(),"Will not receive frame response while unique EMPTY.");
                    }else{
                        waiting.put(unique,waitingResponse=new WaitingResponse(frame,callback){
                            @Override
                            public void run() {
                                waiting.remove(unique);
                                Debug.D(getClass(),"Wait frame response timeout.");
                                handler.removeCallbacks(this);
                                callback.onResponse(What.WHAT_TIMEOUT,"Response timeout.",frame,null,null);
                            }
                        });
                    }
                }
                Debug.D(getClass(),"************发送  "+frame.getData());
                boolean sendSucceed=sendBytes(bytes);
                if(null!=waitingResponse&&null!=unique){
                    if (sendSucceed){
                        int timeout=mTimeout;
                        handler.postDelayed(waitingResponse, timeout <= 1000||timeout>60000 ? 10000 : timeout);
                    }else {
                        waiting.remove(unique);
                        callback.onResponse(What.WHAT_FAIL_UNKNOWN,"Send fail.",frame,null,null);
                    }
                }
                return sendSucceed?waitingResponse:null;
            }
        Debug.W(getClass(),"Can't send invalid frame bytes "+(null!=debug?debug:".")+" length="+length);
        return null;
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
        return "Android"+(null!=prefix?prefix:"")+ System.currentTimeMillis() +UUID.randomUUID();
    }

    private Frame createTextFrame(String text,String toAccount,String unique,String debug){
        int length=null!=text?text.length():0;
        if (length<=0){
            Debug.W(getClass(),"Can't create text frame "+(null!=debug?debug:"."));
            return null;
        }
        return new Frame(length,length,toAccount,null!=unique&&unique.length()>0?unique:
                generateUnique("TextFrameFromAndroid"),null,text,null,null,null,null);
    }

//    private Frame createTextFrame(String text,String toAccount,String unique,String debug){
//        final String encoding="utf-8";
//        try {
//            byte[] bodyBytes = null != text && text.length() > 0 ? text.getBytes(encoding) : null;
//            int bodyBytesLength = null != bodyBytes ? bodyBytes.length : -1;
//            if (bodyBytesLength <= 0) {
//                Debug.E(getClass(), "Can't create text frame to " + toAccount + " which body bytes invalid " + (null != debug ? debug : ".") + " " + bodyBytesLength);
//                return null;
//            }
//            return new Frame(bodyBytesLength,bodyBytesLength,toAccount,Frame.FORMAT_TEXT,unique,null,bodyBytes,null,null,null,encoding);
//        }catch (Exception e){
//            Debug.E(getClass(),"Exception create text frame to "+toAccount+" "+(null!=debug?debug:".")+e,e);
//            e.printStackTrace();
//        }
//        return null;
//    }

    private static abstract class WaitingResponse  extends Canceler implements Runnable{
        private final OnResponse mOnResponse;
        private final Frame mFrame;

        protected WaitingResponse(Frame frame,OnResponse response){
            mOnResponse=response;
            mFrame=frame;
        }
    }

}
