package com.merlin.global;

import android.app.Activity;
import android.os.Bundle;
import com.merlin.classes.ActivityLifecycle;
import com.merlin.client.Client;
import com.merlin.client.OnClientConnectChange;
import com.merlin.debug.Debug;
import com.merlin.oksocket.LMSocket;
import com.merlin.server.Frame;

public class Application extends android.app.Application implements ActivityLifecycle.OnActivityCreate {
   private final Client mClient = new Client("www.luckmerlin.com", 5005);


    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycle(this));
        mClient.setOnFrameReceiveListener(mOnFrameReceiveListener);
        mClient.connect((LMSocket.OnSocketConnectListener) (connected, what)->{
            Debug.D(getClass(),"dddddddddd "+connected+" "+what);
            mClient.login("dddd","123456");
        });
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (null!=activity&&activity instanceof OnClientConnectChange){
            ((OnClientConnectChange)activity).onClientConnectChanged(mClient.isConnected(),mClient);
        }
    }


    private final LMSocket.OnFrameReceiveListener mOnFrameReceiveListener=new LMSocket.OnFrameReceiveListener() {
        @Override
        public void onFrameReceived(Frame frame) {
                Debug.D(Application.this.getClass(),"$$$$$$$$ "+frame);
        }
    };
}
