package com.merlin.transport;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.merlin.api.Reply;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Conveyor {
    private final Map<Convey,Conveying> mConveying=new ConcurrentHashMap<>();
    private final ExecutorService mService = Executors.newCachedThreadPool();
    private final Map<OnConveyStatusChange,Long> mListeners=new WeakHashMap<>();
    private int mLimit;
    private final Handler mHandler;

    public interface Callback{

    }

    public Conveyor(Context context, Looper looper){
        this(context,new Handler(null!=looper?looper:Looper.getMainLooper()));
    }

    public Conveyor(Context context, Handler handler){
//        mContext=null!=context?new WeakReference<>(context):null;
        mHandler=null!=handler?handler:new Handler(Looper.getMainLooper());
    }

    public final boolean listener(OnConveyStatusChange listener,int status,String debug){
        if (null!=listener){
            Map<OnConveyStatusChange,Long> reference=mListeners;
            synchronized (reference){
                if (status==ConveyStatus.ADD){
                    reference.put(listener,System.currentTimeMillis());
                    Debug.D(getClass(),"Add conveyor listener "+(null!=debug?debug:"."));
                    return true;
                }
                Debug.D(getClass(),"Remove conveyor listener "+(null!=debug?debug:"."));
                return null!=reference.remove(listener);
            }
        }
        return false;
    }

    public boolean convey(int mode,OnConveyStatusChange callback, String debug, Convey ... conveys){
        if (null!=conveys&&conveys.length>0){
            switch (mode){
                case ConveyStatus.ADD:
                    return add(callback,debug,conveys);
                case ConveyStatus.CANCELED:
                    return cancel(debug,conveys);
            }
        }
        return false;
    }

    public synchronized boolean add(OnConveyStatusChange callback, String debug,Convey ... conveys){
        if (null!=conveys&&conveys.length>0){
            for (Convey child:conveys) {
                if (null==child){
                    Debug.W(getClass(),"Skip add convey which is NULL.");
                    continue;
                }
                if (exist(child)){
                    Debug.W(getClass(), "Skip add convey which already conveying." + child);
                    notifyStatus(ConveyStatus.CANCELED,"Skip add convey which already conveying.",child,null,null,callback);
                    continue;
                }
                Map<Convey, Conveying> conveyingMap=mConveying;
                Debug.D(getClass(),"Transport add "+" "+child);
                final Conveying conveying=new Conveying(ConveyStatus.ADD,null,callback);
                conveyingMap.put(child,conveying);
                notifyStatus(ConveyStatus.ADD,"Transport add.",child,null,mListeners,callback);
                triggerNext(callback,"After convey add.");
            }
            return true;
        }
        return false;
    }

    private boolean triggerNext(OnConveyStatusChange callback,String debug){

        return false;
    }

    public synchronized final boolean start(Convey convey,OnConveyStatusChange callback,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't start convey while convey is NULL "+(null!=debug?debug:"."));
            return false;
        }
        final ExecutorService service =mService;
        if (null==service){
            Debug.W(getClass(),"Can't start convey while service is NULL "+(null!=debug?debug:".")+" "+convey);
            notifyStatus(ConveyStatus.CANCELED,"Convey service is NULL.",convey,null,mListeners,callback);
            return false;
        }
        final Map<Convey, Conveying> conveyingMap=mConveying;
        if (null==conveyingMap){
            Debug.W(getClass(),"Can't start convey while convey map is NULL "+(null!=debug?debug:".")+" "+convey);
            notifyStatus(ConveyStatus.CANCELED,"Convey map is NULL.",convey,null,mListeners,callback);
            return false;
        }
        synchronized (conveyingMap){
           Conveying conveying= conveyingMap.get(convey);
           if (null==conveying){
               conveying=new Conveying(ConveyStatus.IDLE,null,callback);
               conveyingMap.put(convey,conveying);
           }
           int status=conveying.getStatus();
           if (status!=ConveyStatus.IDLE){
               Debug.W(getClass(),"Can't start convey while status not idle "+(null!=debug?debug:".")+" "+status+" "+convey);
               notifyStatus(ConveyStatus.CANCELED,"Status not idle.",null,null,null,callback);
               return false;
           }
        }
        Debug.D(getClass(),"Start convey "+convey+(null!=debug?debug:"."));
        service.submit(new Runnable() {
            @Override
            public void run() {
//             Debug.D(getClass(),"  "+Thread.currentThread().getName());
                convey.start(null,debug);
            }
        });
        return false;
    }

    public boolean cancel(String debug,Convey... conveys){
        if (null!=conveys&&conveys.length>0){
            for(Convey convey:conveys){
                if (null!=convey){
                    Map<Convey, Conveying> conveyingMap=mConveying;
                    synchronized (conveyingMap){
                       Set<Convey> set= conveyingMap.keySet();
                       if (null!=set){
                           for (Convey child:set) {
                                if (null!=child&&child.equals(convey)){
                                    child.cancel(true,"Before remove convey "+(null!=debug?debug:"."));
                                    conveyingMap.remove(child);
                                }
                           }
                       }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public final boolean exist(Object ...conveys){
        if (null!=conveys&&conveys.length>0){
            Map<Convey, Conveying> conveyingMap=mConveying;
            for (Object object:conveys) {
                if (null!=object){
                    synchronized (conveyingMap){
                        Set<Convey> set=conveyingMap.keySet();
                        if (null!=set&&set.size()>0){
                            for (Convey child:set) {
                                if (null!=child&&child.equals(object)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static class Conveying extends ConveyStatus{
        private final OnConveyStatusChange mCallback;

        private Conveying(int status,Object object,OnConveyStatusChange callback){
            super(status,object);
            mCallback=callback;
        }
    }

    private final void notifyStatus(int status,String note, Convey transport,Object data,Map<OnConveyStatusChange,Long> reference, OnConveyStatusChange change){
        final Handler handler=mHandler;
        if (null!=reference&&null!=handler){
            synchronized (reference){
                Set<OnConveyStatusChange> set=reference.keySet();
                if (null!=set){
                    handler.post(()->{
                        for (OnConveyStatusChange child:set) {
                            if (null!=child){
                                child.onConveyStatusChanged(status,transport,data);
                            }
                        }});
                }
            }
        }
        if (null!=change){
            handler.post(()-> change.onConveyStatusChanged(status,transport,data));
        }
    }

}
