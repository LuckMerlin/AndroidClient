package com.merlin.transport;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Conveyor {
    public final static int MODE_REMOVE=123145;
    public final static int MODE_ADDED=123146;
    private final Map<Convey,Conveying> mConveying=new ConcurrentHashMap<>();
    private final ExecutorService mService = Executors.newCachedThreadPool();
    private final Map<OnConveyStatusChange,Long> mListeners=new WeakHashMap<>();
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
                if (status==MODE_ADDED){
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
                case MODE_ADDED:
                    return add(callback,debug,conveys);
                case MODE_REMOVE:
                    return remove(debug,conveys);
            }
        }
        return false;
    }


    public final synchronized boolean cancel(boolean cancel, String debug,AbsTransport ...transports){
//        Map<AbsTransport, Transporter.Transporting<Retrofit.Canceler>> map=null!=transports&&transports.length>0?mTransporting:null;
//        if (null!=map){
//            for (AbsTransport child:transports) {
//                Transporter.Transporting<Retrofit.Canceler> transporting=null!=child?map.get(child):null;
//                Retrofit.Canceler canceler=null!=transporting?transporting.mCanceler:null;
//                if (null!=canceler&&canceler.cancel(cancel,debug)&&null!=map.remove(child)){
////            notifyStatusChange(TRANSPORT_CANCEL, transport,debug);
//                }
//            }
//            return true;
//        }
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
                    notifyStatusChange(MODE_REMOVE,child,null,callback);
                    continue;
                }
                Map<Convey, Conveying> conveyingMap=mConveying;
                Debug.D(getClass(),"Transport add "+" "+child);
                final Conveying conveying=new Conveying(callback);
                conveyingMap.put(child,conveying);
                notifyStatusChange(MODE_ADDED,child,null,callback);
                triggerNext("After convey add.");
            }
            return true;
        }
        return false;
    }

    private boolean triggerNext(String debug){

        return false;
    }

    public boolean remove(String debug,Convey... conveys){
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

    private static class Conveying{
        private final OnConveyStatusChange mCallback;
        private Conveying(OnConveyStatusChange callback){
            mCallback=callback;
        }
    }


    private final void notifyStatusChange(int status, Convey transport,Object data, OnConveyStatusChange change){
        Map<OnConveyStatusChange,Long> reference=mListeners;
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
