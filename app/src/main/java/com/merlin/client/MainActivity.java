package com.merlin.client;

import android.view.View;

import com.merlin.activity.SocketActivity;
import com.merlin.client.databinding.ActivityMainBinding;
import com.merlin.debug.Debug;
import com.merlin.model.LoginModel;
import com.merlin.oksocket.LMSocket;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.server.Frame;


public class MainActivity extends SocketActivity<ActivityMainBinding,LoginModel> implements OnFrameReceive {

    public void ddd(View view){
        System.exit(1);
    }

    @Override
    public void onFrameReceived(Frame frame, Client client) {

    }
}
