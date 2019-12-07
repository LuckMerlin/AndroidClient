package com.merlin.global;

import android.app.Activity;
import android.os.Bundle;

import com.merlin.classes.ActivityLifecycle;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.oksocket.OnFrameReceive;

public class Application extends android.app.Application implements ActivityLifecycle.OnActivityCreate {
   private final Client mClient = new Client("www.luckmerlin.com", 5005);
   private final ActivityLifecycle mActivityLifecycle=new ActivityLifecycle(this);
   private final Invoker mInvoker=new Invoker();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycle);
        mClient.setOnFrameReceive(mOnFrameReceiveListener);
        mClient.setOnClientStatusChange(mStatusChange);
        mClient.connect((connected,what)->{
             if (connected){
                 mClient.login("wuyue","123456");
             }
        });
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    private final OnFrameReceive mOnFrameReceiveListener=(frame,client)->{
        ActivityLifecycle lifecycle=mActivityLifecycle;
        Activity activity=null!=lifecycle?lifecycle.getTopActivity():null;
        if (null!=activity) {
            mInvoker.invoke(OnFrameReceive.class,activity).onFrameReceived(frame,client);
        }
    };

    private final OnClientStatusChange mStatusChange=(auto,what,data,client)->{
        ActivityLifecycle lifecycle=mActivityLifecycle;
        Activity activity=null!=lifecycle?lifecycle.getTopActivity():null;
        if (null!=activity) {
            mInvoker.invoke(OnClientStatusChange.class,activity).onClientStatusChanged(auto,what,data,client);
        }
    };
}
