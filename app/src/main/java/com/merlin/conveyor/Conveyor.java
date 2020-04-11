package com.merlin.conveyor;

import android.os.Handler;
import android.os.Looper;

import com.merlin.api.Reply;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Conveyor {
    private final Map<Convey,Conveying> mConveying=new ConcurrentHashMap<>();
    private final Map<OnConveyStatusChange,Long> mListeners=new WeakHashMap<>();
    private int mLimit;
    private final Handler mHandler;
    private final Retrofit mRetrofit;
    public interface Callback{

    }

    public Conveyor(Retrofit retrofit,Looper looper){
        this(retrofit,new Handler(null!=looper?looper:Looper.getMainLooper()));
    }

    public Conveyor(Retrofit retrofit,Handler handler){
        mHandler=null!=handler?handler:new Handler(Looper.getMainLooper());
        mRetrofit=null!=retrofit?retrofit:new Retrofit();
    }

    public final boolean listener(int status,OnConveyStatusChange listener,String debug){
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
                    Debug.W(getClass(),"Skip add convey which is NULL "+(null!=debug?debug:"."));
                    continue;
                }
                if (exist(child)){
                    Debug.W(getClass(), "Skip add convey which already conveying "+(null!=debug?debug:".") + child);
                    notifyStatus(ConveyStatus.CANCELED,"Skip add convey which already conveying.",child,null,null,callback);
                    continue;
                }
                Map<Convey, Conveying> conveyingMap=mConveying;
                Debug.D(getClass(),"Transport add "+" "+child+" "+(null!=debug?debug:"."));
                final Conveying conveying=new Conveying(callback);
                conveyingMap.put(child,conveying);
                notifyStatus(ConveyStatus.ADD,"Transport add.",child,null,mListeners,callback);
                triggerNext("After convey add.");
            }
            return true;
        }
        return false;
    }

    private boolean triggerNext(String debug){
        if (isLimitTouched()){//Not need trigger next NOW
            Debug.D(getClass(),"Not need trigger next convey while limit touched "+(null!=debug?debug:".")+mLimit);
            return false;
        }
        Convey convey=findNextInStatus(ConveyStatus.IDLE);
        return null!=convey&&start(convey,null,"While trigger next "+(null!=debug?debug:"."));
    }

    private synchronized final boolean start(Convey convey,OnConveyStatusChange callback,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't start convey while convey is NULL "+(null!=debug?debug:"."));
            return false;
        }
        final Retrofit retrofit =mRetrofit;
        if (null==retrofit){
            Debug.W(getClass(),"Can't start convey while retrofit is NULL "+(null!=debug?debug:".")+" "+convey);
            notifyStatus(ConveyStatus.CANCELED,"Convey retrofit is NULL.",convey,null,mListeners,callback);
            return false;
        }
        final Map<Convey, Conveying> conveyingMap=mConveying;
        if (null==conveyingMap){
            Debug.W(getClass(),"Can't start convey while convey map is NULL "+(null!=debug?debug:".")+" "+convey);
            notifyStatus(ConveyStatus.CANCELED,"Convey map is NULL.",convey,null,mListeners,callback);
            return false;
        }
        Conveying conveying;
        synchronized (conveyingMap){
            conveying= conveyingMap.get(convey);
           if (null==conveying){
               conveyingMap.put(convey,conveying=new Conveying(callback));
               notifyStatus(ConveyStatus.ADD,"While convey start.",convey,null,mListeners,callback);
           }
           int status=conveying.getStatus();
           if (status!=ConveyStatus.IDLE){
               Debug.W(getClass(),"Can't start convey while status not idle "+(null!=debug?debug:".")+" "+status+" "+convey);
               notifyStatus(ConveyStatus.CANCELED,"Status not idle.",convey,null,mListeners,callback);
               return false;
           }
        }
        Debug.D(getClass(),"Start convey "+convey+(null!=debug?debug:"."));
        final  OnConveyStatusChange change=new OnConveyStatusChange() {
            @Override
            public void onConveyStatusChanged(int status, Convey convey, Object data) {

            }
        };
        final Step step=(Reply reply) ->{

        };
        convey.start(step,mRetrofit,change,debug);
//        final OnConveyStatusChange innerChange=( status, innerConvey, data)-> {
//                if (null!=callback){
//                    callback.onConveyStatusChanged(status,innerConvey,data);
//                }
//            };
//
//        final Convey.Finisher finisher=new Convey.Finisher() {
//            @Override
//            public void onFinish(Reply reply) {
//                if (null!=reply&&null!=conveying1){//Save reply
//                    conveying1.updateStatus(ConveyStatus.FINISHED,reply);
//                }
//                notifyStatus(ConveyStatus.FINISHED,"Finished.",convey,reply,mListeners,callback);
//            }
//
//            @Override
//            public void onProgress(long conveyed, long total, float speed, Convey c) {
//                notifyStatus(ConveyStatus.PROGRESS,"Progress.",convey,c,mListeners,callback);
//            }
//        };
//        notifyStatus(ConveyStatus.CREATE,"Convey create.",convey,null,mListeners,callback);
//        Reply reply=convey.start(retrofit,finisher,innerChange,debug);
//        if (null!=reply&&null!=conveying1){//Save reply
//            conveying1.updateStatus(ConveyStatus.FINISHED,reply);
//        }
        return false;
    }

    public synchronized boolean isLimitTouched(){
       int limit=getLimit();
       if (limit<=0){
           return true;
       }
       List<Convey> list=getStatusConveys(ConveyStatus.STARTED);
       return null!=list&&list.size()>=limit;
    }

    public List<Convey> getStatusConveys(int ...status){
        if (null!=status&&status.length>0){
            List<Convey> list=new ArrayList<>();
            for (int child:status) {
                Map<Convey,Conveying> map=mConveying;
                if (null!=map){
                    synchronized (map){
                        Set<Convey> set=map.keySet();
                        if (null!=set){
                            for (Convey convey:set) {
                                if (null!=convey&&convey.isStatus(child)){
                                    list.add(convey);
                                }
                            }
                        }
                    }
                }
            }
            return null!=list&&list.size()>0?list:null;
        }
        return null;
    }

    public int getLimit(){
        int limit=mLimit;
        return limit<=0?1:limit;
    }

    public Convey findNextInStatus(int ...status){
        if (null!=status&&status.length>0){
            for (int child:status) {
                Map<Convey, Conveying> map = mConveying;
                if (null != map) {
                    synchronized (map) {
                        Set<Convey> set = map.keySet();
                        if (null != set) {
                            for (Convey convey : set) {
                                if (null != convey && convey.isStatus(child)) {
                                    return convey;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<Convey> get(Class<? extends Convey> cls, int... status){
        Map<Convey, Conveying> conveyingMap=mConveying;
        if(null!=conveyingMap){
            final List<Convey> result=new ArrayList<>();
            synchronized (conveyingMap){
                Set<Convey> set= conveyingMap.keySet();
                if (null!=set){
                    for (Convey child:set) {
                        if (null!=child&&((null==cls||child.getClass().isAssignableFrom(cls))&&
                                (null==status||status.length<=0||child.isStatus(status)))){
                            result.add(child);
                        }
                    }
                }
            }
            return null!=result&&result.size()>0?result:null;
        }
        return null;
    }

    public boolean cancel(String debug,Convey ...conveys){
        if (null!=conveys&&conveys.length>0){
            for(Convey convey:conveys){
                if (null!=convey){
                    Map<Convey, Conveying> conveyingMap=mConveying;
                    synchronized (conveyingMap){
                       Set<Convey> set= conveyingMap.keySet();
                       if (null!=set){
                           for (Convey child:set) {
                                if (null!=child&&child.equals(convey)&&(child.isStatus(Status.FINISHED)
                                        ||child.cancel(mRetrofit,true,"Before remove convey "+(null!=debug?debug:".")))){
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

        private Conveying(OnConveyStatusChange callback){
            this(Status.IDLE,null,callback);
        }

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

    public interface Step{
        void reply(Reply reply);
    }
}
