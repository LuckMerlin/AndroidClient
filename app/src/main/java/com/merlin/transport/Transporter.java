package com.merlin.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.merlin.api.Reply;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class Transporter implements Callback{
    public final static int TYPE_NONE =0x00;//0000 0000
    public final static int TYPE_DOWNLOAD =0x01;//0000 0001
    public final static int TYPE_UPLOAD =0x02;//0000 0010
    public final static int TYPE_ALL =TYPE_DOWNLOAD&TYPE_UPLOAD;
    private Map<AbsTransport, Transporting<Retrofit.Canceler>> mTransporting;
    private final Handler mHandler;
    private WeakReference<Context> mContext;
    private final Map<OnStatusChange,Long> mListeners=new WeakHashMap<>();
    private final Retrofit mRetrofit=new Retrofit();
    private int mMaxVolume;
    private final ExecutorService mService = Executors.newCachedThreadPool();

    public Transporter(Context context, Looper looper){
        this(context,new Handler(null!=looper?looper:Looper.getMainLooper()));
    }

    public Transporter(Context context, Handler handler){
        mContext=null!=context?new WeakReference<>(context):null;
        mHandler=null!=handler?handler:new Handler(Looper.getMainLooper());
    }

    public final boolean listener(OnStatusChange listener,int status,String debug){
        if (null!=listener){
            Map<OnStatusChange,Long> reference=mListeners;
            synchronized (reference){
                if (status==TRANSPORT_ADD){
                    reference.put(listener,System.currentTimeMillis());
                    Debug.D(getClass(),"Add transporter listener "+(null!=debug?debug:"."));
                    return true;
                }
                Debug.D(getClass(),"Remove transporter listener "+(null!=debug?debug:"."));
                return null!=reference.remove(listener);
            }
        }
        return false;
    }

    private final void notifyStatusChange(int status, AbsTransport transport,Object data, OnStatusChange change){
        Map<OnStatusChange,Long> reference=mListeners;
        final Handler handler=mHandler;
        if (null!=reference&&null!=handler){
            synchronized (reference){
                Set<OnStatusChange> set=reference.keySet();
                if (null!=set){
                    handler.post(()->{
                    for (OnStatusChange child:set) {
                        if (null!=child){
                            child.onStatusChanged(status,transport,data);
                        }
                    }});
                }
            }
        }
        if (null!=change){
            handler.post(()-> change.onStatusChanged(status,transport,data));
        }
    }

    protected final Context getContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    protected final boolean toast(int textId){
        Context context=getContext();
        String text=null!=context?context.getString(textId):null;
        return null!=text&&null!=context&&toast(text);
    }

    protected final boolean toast(String text){
        Context context=getContext();
        if (null!=context&&null!=text){
            if (Looper.getMainLooper()!=Looper.myLooper()){
                return new Handler(Looper.getMainLooper()).post(()->Toast.makeText(context,text,Toast.LENGTH_SHORT).show());
            }
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public final boolean run(int status, boolean interactive, String debug,AbsTransport... transports){
        if (null!=transports&&transports.length>0) {
            switch (status) {
                case TRANSPORT_ADD:
                    return add(interactive,null,debug,transports);
                case TRANSPORT_CANCEL:
                case TRANSPORT_REMOVE:
                    return cancel(true,debug,transports);
            }
        }
        return false;
    }

    public final synchronized boolean cancel(boolean cancel, String debug,AbsTransport ...transports){
        Map<AbsTransport, Transporting<Retrofit.Canceler>> map=null!=transports&&transports.length>0?mTransporting:null;
        if (null!=map){
            for (AbsTransport child:transports) {
                Transporting<Retrofit.Canceler> transporting=null!=child?map.get(child):null;
                Retrofit.Canceler canceler=null!=transporting?transporting.mCanceler:null;
                if (null!=canceler&&canceler.cancel(cancel,debug)&&null!=map.remove(child)){
//            notifyStatusChange(TRANSPORT_CANCEL, transport,debug);
                }
            }
            return true;
        }
        return false;
    }

    public final synchronized boolean add(boolean interactive, OnStatusChange progress, String debug,AbsTransport... transports) {
        if (null!=transports&&transports.length>0){
            for (AbsTransport child:transports) {
                add(child,interactive,progress,debug);
            }
        }
        return false;
    }

    public final synchronized boolean add(final AbsTransport transport, boolean interactive, OnStatusChange progress, String debug) {
        if (null==transport){
            Debug.W(getClass(),"Skip add transport file which is NULL.");
            return false;
        }
        Map<AbsTransport, Transporting<Retrofit.Canceler>> transportingMap=mTransporting;
        transportingMap=null!=transportingMap?transportingMap:(mTransporting=new ConcurrentHashMap<>());
        Set<AbsTransport> set=transportingMap.keySet();
        final int size=null!=set?set.size():-1;
        if (size>=0){
            for (AbsTransport child:set) {
                if (null!=child&&child.equals(transport)) {
                    Debug.W(getClass(), "Skip add transport file which already transporting." + transport);
                    notifyStatusChange(TRANSPORT_REMOVE, transport, progress, null);
                    return false;
                }
            }
        }
        Debug.D(getClass(),"Transport add "+" "+transport);
        final Transporting<Retrofit.Canceler> transporting=new Transporting<>();
        transportingMap.put(transport,transporting);
        notifyStatusChange(TRANSPORT_ADD,transport,null,progress);
        int maxVolume=mMaxVolume;
        maxVolume=maxVolume<=0?1:maxVolume;
        transporting.mState=TRANSPORT_QUEUING;
        if (maxVolume<size){
            transporting.mChange=progress;
            notifyStatusChange(TRANSPORT_QUEUING, transport, progress, null);
            return true;
        }
        return transport(transport,progress,debug);
    }

    public boolean transportNext(String debug){
        Map<AbsTransport, Transporting<Retrofit.Canceler>> map=mTransporting;
        if (null!=map){
            int maxVolume=mMaxVolume;
            maxVolume=maxVolume<=0?1:maxVolume;
            synchronized (map){
                Set<AbsTransport> set=null!=map?map.keySet():null;
                Debug.D(getClass(),"Try switch next queuing transport "+(null!=debug?debug:"."));
                if (null!=set&&set.size()>0){
                    for (AbsTransport child:set) {
                        Transporting<Retrofit.Canceler> canceler= null!=child?map.get(child):null;
                        if (null!=canceler&&canceler.mState==TRANSPORT_QUEUING){
                            return transport(child,canceler.mChange,debug);
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean transport(AbsTransport transport,OnStatusChange change,String debug){
        if (null!=transport){
            Map<AbsTransport, Transporting<Retrofit.Canceler>> map=mTransporting;
            final Transporting<Retrofit.Canceler> transporting=null!=map?map.get(transport):null;
            if (null!=transporting){
                final OnTransportUpdate update=(finish,what,  note,  data)->{
                    if (null!=data&&data instanceof Progress){
                        transport.setSize(((Progress)data).getDoneSize());
                        transport.setTotal(((Progress)data).getTotalSize());
                        transport.setSpeed(((Progress)data).getSpeed());
                    }
                    if (null!=map){
                        map.remove(transport);
                    }
                    notifyStatusChange(what,transport,data,change);
//                    Handler handler=finish&&interactive&&null!=note&&note.length()>0?mHandler:null;
//                    if (null!=handler){
//                        handler.post(()->toast(note));
//                    }
                };
                final Callable callable=(Callable<Reply<?>>) ()-> {
                    transporting.mCanceler=transport.onStart(new OnTransportUpdate() {
                        @Override
                        public void onTransportUpdate(boolean finish, int what, String note, Object data) {
                            if (null!=update) {
                                update.onTransportUpdate(finish, what, note, data);
                            }
                            if (finish){
                                transportNext("After one transport finish.");
                            }
                        }
                    }, mRetrofit);
                    return null;};
                mService.submit(callable);
                return true;
            }
        }
        return false;
    }

    public final Collection<AbsTransport> getTransporting(String name){
        Map<AbsTransport, Transporting<Retrofit.Canceler>> transporting=mTransporting;
        return null!=transporting?transporting.keySet():null;
    }

    public final boolean isTransporting(Object ...objects){
        Map<AbsTransport, Transporting<Retrofit.Canceler>> transporting=null!=objects&&objects.length>0?mTransporting:null;
        if (null!=transporting){
            synchronized (transporting){
                for (Object object:objects) {
                    if (null!=object&&transporting.containsKey(object)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class Transporting<T>{
        private int mState=TRANSPORT_NONE;
        private T mCanceler;
        private OnStatusChange mChange;

    }
}
