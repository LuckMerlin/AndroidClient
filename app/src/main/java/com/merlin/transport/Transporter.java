package com.merlin.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public final class Transporter implements Callback{
    public final static int TYPE_NONE =0x00;//0000 0000
    public final static int TYPE_DOWNLOAD =0x01;//0000 0001
    public final static int TYPE_UPLOAD =0x02;//0000 0010
    public final static int TYPE_ALL =TYPE_DOWNLOAD&TYPE_UPLOAD;
    private Map<Transport, Transporting<Canceler>> mTransporting;
    private final Handler mHandler;
    private WeakReference<Context> mContext;
    private final Map<OnStatusChange,Long> mListeners=new WeakHashMap<>();
    private final Retrofit mRetrofit=new Retrofit();

    public Transporter(Context context){
         this(context,null);
    }

    public Transporter(Context context, Looper looper){
        mContext=null!=context?new WeakReference<>(context):null;
        mHandler=new Handler(null!=looper?looper:Looper.getMainLooper());
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

    private final void notifyStatusChange(int status, Transport transport,OnStatusChange change){
        Map<OnStatusChange,Long> reference=mListeners;
        final Handler handler=mHandler;
        if (null!=reference&&null!=handler){
            synchronized (reference){
                Set<OnStatusChange> set=reference.keySet();
                if (null!=set){
                    handler.post(()->{
                    for (OnStatusChange child:set) {
                        if (null!=child){
                            child.onStatusChanged(status,transport);
                        }
                    }});
                }
            }
        }
        if (null!=change){
            handler.post(()-> change.onStatusChanged(status,transport));
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


    public final synchronized boolean add(final Transport transport, boolean interactive, OnStatusChange progress, String debug) {
        if (null==transport){
            Debug.W(getClass(),"Skip add transport file which is NULL.");
            return false;
        }
        Map<Transport, Transporting<Canceler>> transportingMap=mTransporting;
        transportingMap=null!=transportingMap?transportingMap:(mTransporting=new ConcurrentHashMap<>());
        if (transportingMap.containsKey(transport)){
            Debug.W(getClass(),"Skip add transport file which already transporting."+transport);
            notifyStatusChange(TRANSPORT_REMOVE,transport,progress);
            return false;
        }
        final Transporting<Canceler> transporting=new Transporting<>();
        transportingMap.put(transport,transporting);
        notifyStatusChange(TRANSPORT_ADD,transport,progress);
        Debug.D(getClass(),"Transport add "+transport);
        final OnTransportUpdate update=(finish,what,  note,  uploaded, total, speed)->{
                if (null!=uploaded){
                    transport.setSize(uploaded);
                }
                if (null!=total){
                    transport.setTotal(total);
                }
                if (null!=speed){
                    transport.setSpeed(speed);
                }
                Map<Transport, Transporting<Canceler>> map=finish?mTransporting:null;
                if (null!=map){
                    map.remove(transport);
                }
                notifyStatusChange(what,transport,progress);
        };
        Canceler data=transport.onStart(update,mRetrofit);
        if (null!=data){
            transporting.mCanceler=data;
        }
        return null!=data;
    }

    public final Collection<Transport> getTransporting(String name){
        Map<Transport, Transporting<Canceler>> transporting=mTransporting;
        return null!=transporting?transporting.keySet():null;
    }

    public final boolean isTransporting(Object ...objects){
        Map<Transport, Transporting<Canceler>> transporting=null!=objects&&objects.length>0?mTransporting:null;
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
        private T mCanceler;

    }
}
