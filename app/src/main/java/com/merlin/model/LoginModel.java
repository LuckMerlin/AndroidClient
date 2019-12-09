package com.merlin.model;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.Socket;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.protocol.What;
import com.merlin.server.Frame;

public class LoginModel extends BaseModel implements OnFrameReceive, OnClientStatusChange {

    public LoginModel(Context context){
        super(context);
    }

    @Override
    public void onViewClick(View v, int id) {
        Toast.makeText(v.getContext(),"dianjie "+v,Toast.LENGTH_LONG).show();
        System.exit(1);
    }

    @Override
    public void onClientStatusChanged(boolean autoNotify, int what, Object data, Client client) {
    }

    @Override
    public void onFrameReceived(Frame frame, Client client){
        if (frame.getResponse().getWhat() == What.WHAT_CLIENT_ONLINE){
            Debug.D(getClass(),"有人上线了  "+" "+frame.getBodyText());
            client.sendBytesTo(Frame.encodeString("获取李彪"), "linqiang", new Socket.OnRequestFinish() {
                @Override
                public void onRequestFinish(boolean succeed, int what, Frame frame) {
                    Debug.D(getClass(),"获取收到结果了 "+frame);
                }
            });
        }
    }
}
