package com.merlin.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MultipartBody;

public abstract class Transporter<M extends Transport,N extends Canceler> extends Retrofit implements Callback{
    public final static int TYPE_NONE =0x00;//0000 0000
    public final static int TYPE_DOWNLOAD =0x01;//0000 0001
    public final static int TYPE_UPLOAD =0x02;//0000 0010
    public final static int TYPE_ALL =TYPE_DOWNLOAD&TYPE_UPLOAD;
    private final Map<M, N> mTransporting=new ConcurrentHashMap<>();
    private final Handler mHandler;
    private WeakReference<Context> mContext;
    private final Map<OnStatusChange,Long> mListeners=new WeakHashMap<>();

    public Transporter(Context context){
         this(context,null);
    }

    public Transporter(Context context,Looper looper){
        mContext=null!=context?new WeakReference<>(context):null;
        mHandler=new Handler(null!=looper?looper:Looper.getMainLooper());
    }

    public final boolean listener(OnStatusChange listener,int status,String debug){
        if (null!=listener){
            Map<OnStatusChange,Long> reference=mListeners;
            synchronized (reference){
                if (status==TRANSPORT_ADD){
                    reference.put(listener,System.currentTimeMillis());
                    return true;
                }
                return null!=reference.remove(listener);
            }
        }
        return false;
    }

    private final void notifyStatusChange(int status, Transport transport,OnStatusChange change){
        Map<OnStatusChange,Long> reference=mListeners;
        Handler handler=mHandler;
        if (null!=reference){
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
            handler.post(()->{
            change.onStatusChanged(status,transport);});
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

    protected abstract N onAddTransport(M transport,TransportUpdate update,boolean interactive);

    public final synchronized boolean add(M transport, boolean interactive, OnStatusChange progress, String debug) {
        if (null==transport){
            Debug.W(getClass(),"Skip add transport file which is NULL.");
            return false;
        }
        final Map<M, N> transporting=mTransporting;
        if (null!=transporting&&transporting.containsKey(transport)){
            Debug.W(getClass(),"Skip add transport file which already transporting."+transport);
            notifyStatusChange(TRANSPORT_REMOVE,transport,progress);
            return false;
        }
        Debug.D(getClass(),"Transport add "+transport);
        notifyStatusChange(TRANSPORT_ADD,transport,progress);
        final TransportUpdate update=new TransportUpdate(){
            @Override
            public void onTransportFinish(boolean succeed) {
                notifyStatusChange(TRANSPORT_REMOVE,transport,progress);
            }

            @Override
            public void onTransportProgress(long uploaded, long total,float speed) {
                transport.setSize(uploaded);
                transport.setTotal(total);
                transport.setSpeed(speed);
                notifyStatusChange(TRANSPORT_PROGRESS,transport,progress);
            }
        };
        N data=onAddTransport(transport,update,interactive);
        if (null==data){
            notifyStatusChange(TRANSPORT_REMOVE,transport,progress);
            return false;
        }
        return true;
    }

    public final Collection<M> getTransporting(String name){
        Map<M, N> transporting=mTransporting;
        return null!=transporting?transporting.keySet():null;
    }

    public final boolean isTransporting(Object ...objects){
        Map<M, N> transporting=null!=objects&&objects.length>0?mTransporting:null;
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

    protected  interface TransportUpdate{
        void onTransportProgress(long uploaded, long total,float speed);
        void onTransportFinish(boolean succeed);
    }
}
