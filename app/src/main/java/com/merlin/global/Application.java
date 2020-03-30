package com.merlin.global;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.merlin.api.Address;
import com.merlin.classes.ActivityLifecycle;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.socket.Socket;

import java.util.List;

public class Application extends android.app.Application implements ActivityLifecycle.OnActivityCreate {
   private final ActivityLifecycle mActivityLifecycle=new ActivityLifecycle(this);
   private final Socket mSocket=new Socket(Address.HOST.replace("http://",""),Address.PORT+1);
   private final Clients mClients=new Clients();

    @Override
   public void onCreate() {
        super.onCreate();
        new Clients().init(this,"");
        registerActivityLifecycleCallbacks(mActivityLifecycle);
    }

   public final static Socket getSocket(Context context){
       context=null!=context?context.getApplicationContext():null;
        return null!=context&&context instanceof Application?((Application)context).mSocket:null;
   }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    private final OnFrameReceive mOnFrameReceiveListener=(frame,client)->{
        ActivityLifecycle lifecycle=mActivityLifecycle;
        Activity activity=null!=lifecycle?lifecycle.getTopActivity():null;
//        if (null!=activity) {
//            mInvoker.invoke(OnFrameReceive.class,activity).onFrameReceived(frame,client);
//        }
    };

    private final OnClientStatusChange mStatusChange=(auto,what,data,client)->{
        ActivityLifecycle lifecycle=mActivityLifecycle;
        Activity activity=null!=lifecycle?lifecycle.getTopActivity():null;
//        if (null!=activity) {
//            mInvoker.invoke(OnClientStatusChange.class,activity).onClientStatusChanged(auto,what,data,client);
//        }
    };

    public static Application get(Context context){
        context=null!=context?context instanceof Application?context:context.getApplicationContext():context;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    public List<Activity> finishAllActivity(Object ...activities){
        ActivityLifecycle lifecycle=mActivityLifecycle;
        return null!=lifecycle&&null!=activities&&activities.length>0?lifecycle.finishAllActivity(activities):null;
    }

}
