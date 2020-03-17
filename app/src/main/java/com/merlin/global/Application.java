package com.merlin.global;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.merlin.api.Address;
import com.merlin.classes.ActivityLifecycle;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.oksocket.OnClientStatusChange;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.socket.Canceler;
import com.merlin.socket.Frame;
import com.merlin.socket.OnConnectFinish;
import com.merlin.socket.OnResponse;
import com.merlin.socket.Socket;
import com.merlin.util.Byte;
import com.merlin.util.Int;

import java.io.File;
import java.util.List;

public class Application extends android.app.Application implements ActivityLifecycle.OnActivityCreate {
   private final ActivityLifecycle mActivityLifecycle=new ActivityLifecycle(this);
   private final Socket mSocket=new Socket(Address.HOST.replace("http://",""),Address.PORT+1);

    @Override
   public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(mActivityLifecycle);
//        mSocket.connect(null,"While application onCreate");
        test(null);
    }

   public final static Socket getSocket(Context context){
       context=null!=context?context.getApplicationContext():null;
        return null!=context&&context instanceof Application?((Application)context).mSocket:null;
   }

    private void test(String unique){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
//                mSocket.downloadFile("./生日歌.mp3", 0f, null, new OnResponse() {
//                    @Override
//                    public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
//                        Debug.D(getClass()," "+Thread.currentThread().getName()+" "+
//                                (null!=response?response.getBody():null)+" "+response.isTerminal());
//                        return NEXT_FRAME;
//                    }
//                },"");
//                String localPath,String toAccount,String folder,String name,OnResponse callback,String debug
//                  mSocket.uploadFile(new File("/sdcard/Musics/大壮 - 我们不一样.mp3"), null, "asfdasdfa",
//                          "操蛋.mp3", new OnResponse() {
//                      @Override
//                      public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
//                          if (null!=response){
//                                byte[] body=response.getBody();
//                                Debug.D(getClass(),"%% "+response.isTerminal()+"\n"
//                                    +" "+(null!=body?body.length:-1)+" \n");
//                                return NEXT_FRAME;
//                            }
//                          return null;
//                      }
//                  }, null);
////                mSocket.downloadFile("./生日歌.mp3", 0f,null, new OnResponse() {
////                    @Override
////                    public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
////                        if (null!=response){
////                            byte[] body=response.getBody();
////                            Debug.D(getClass(),"%% "+response.isTerminal()+"\n"+
////                                    (response.isFormat(Frame.FORMAT_TEXT)?response.getBodyText():"")
////                                    +" "+(null!=body?body.length:-1)+" \n");
////                            return NEXT_FRAME;
////                        }
////                        return null;
////                    }
////                },"test.");
////                mSocket.sendText("我爱中国操蛋", null,unique, new OnResponse() {
////                    @Override
////                    public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
////                        Debug.D(getClass(),"SSS "+what+ " "+note+" "+(null!=response?response.getBodyText(null):null));
////                        new Handler(Looper.getMainLooper()).postDelayed(()->{test(null!=response?response.getUnique():null);},4000);
////                        return null;
////                    }
////                },null);
////                new Handler(Looper.getMainLooper()).postDelayed(this,4000);
            }
        }, 5000);
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
