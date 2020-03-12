package com.merlin.global;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.merlin.api.Address;
import com.merlin.classes.ActivityLifecycle;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.oksocket.Socket;

import java.util.List;

public class Application extends android.app.Application implements ActivityLifecycle.OnActivityCreate {
   private final Client mClient = new Client(
//              "172.16.20.215",5005);
//              "127.0.0.1",5006);
           "http://192.168.0.6", 2009);
//           "www.luckmerlin.com", 5005);
   private final ActivityLifecycle mActivityLifecycle=new ActivityLifecycle(this);
   private final Invoker mInvoker=new Invoker();
   private final Socket mSocket=new Socket(Address.HOST,Address.PORT+1);

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycle);
        mSocket.connect(new Socket.OnSocketConnectChange() {
            @Override
            public void onSocketConnectChanged(boolean connected, int what) {
                Debug.D(getClass(),"AAAA "+connected+" "+what);
            }
        });
//        mClient.setOnFrameReceive(mOnFrameReceiveListener);
//        mClient.putListener(mStatusChange);
//        mClient.connect((connected,what)->{
//            Debug.D(getClass(),"Connected ", mStatusChange);
//             if (connected){
////                 mClient.login("wuyue","123456");
//             }
//        });
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

    public static Application get(Context context){
        context=null!=context?context instanceof Application?context:context.getApplicationContext():context;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    public Client getClient() {
        return mClient;
    }


    public List<Activity> finishAllActivity(Object ...activities){
        ActivityLifecycle lifecycle=mActivityLifecycle;
        return null!=lifecycle&&null!=activities&&activities.length>0?lifecycle.finishAllActivity(activities):null;
    }



}
