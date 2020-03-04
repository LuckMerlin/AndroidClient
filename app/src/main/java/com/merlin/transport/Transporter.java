package com.merlin.transport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.merlin.server.Retrofit;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class Transporter extends Retrofit {
    public final static int TYPE_NONE =0x00;//0000 0000
    public final static int TYPE_DOWNLOAD =0x01;//0000 0001
    public final static int TYPE_UPLOAD =0x02;//0000 0010
    public final static int TYPE_ALL =TYPE_DOWNLOAD&TYPE_UPLOAD;
    private final Map<OnStatusChange,Long> mListeners=new WeakHashMap<>();
    public interface Callback{
        int TRANSPORT_ADD=123;
        int TRANSPORT_PROGRESS=124;
        int TRANSPORT_REMOVE=125;
        int TRANSPORT_PAUSE=126;
        int TRANSPORT_START=127;
    }

    public interface OnStatusChange extends Callback{
        void onStatusChanged(int status,Transport transport);
    }

    /**
     * @deprecated
     */
    public interface OnTransportProgress extends Callback{
        void onTransportProgress(Transport transport,long upload,long total);
    }

    public final boolean listener(OnStatusChange listener,int status,String debug){
        if (null!=listener){
            Map<OnStatusChange,Long> reference=mListeners;
            synchronized (reference){
                if (status==Callback.TRANSPORT_ADD){
                    reference.put(listener,System.currentTimeMillis());
                    return true;
                }
                return null!=reference.remove(listener);
            }
        }
        return false;
    }

    protected final void notifyStatusChange(int status, Transport transport,OnStatusChange change){
        Map<Uploader.OnStatusChange,Long> reference=mListeners;
        if (null!=reference){
            synchronized (reference){
                Set<Uploader.OnStatusChange> set=reference.keySet();
                if (null!=set){
                    for (OnStatusChange child:set) {
                        if (null!=child){
                            child.onStatusChanged(status,transport);
                        }
                    }
                }
            }
        }
        if (null!=change){
            change.onStatusChanged(status,transport);
        }
    }

    protected abstract Context getContext();

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

}
