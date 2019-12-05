package com.merlin.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.merlin.debug.Debug;
import com.merlin.server.FrameReader;
import com.merlin.server.Heartbeat;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectionInfo info = new ConnectionInfo("www.luckmerlin.com", 5005);
        final IConnectionManager manager=OkSocket.open(info);
        manager.registerReceiver(new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                Debug.D(getClass(), "连接成功");
                manager.getPulseManager().setPulseSendable(new Heartbeat()).pulse();
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
            }

            @Override
            public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
                super.onSocketWriteResponse(info, action, data);
                Debug.D(getClass(), "onSocketWriteResponse");
            }

            @Override
            public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
                super.onPulseSend(info, data);
                manager.getPulseManager().feed();
            }
        });
        OkSocketOptions options= manager.getOption();

        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        builder.setReaderProtocol(new FrameReader());
        builder.setPulseFrequency(5000);
        manager.option(builder.build());
        manager.connect();
    }

    public void ddd(View view){
        System.exit(1);
    }


}
