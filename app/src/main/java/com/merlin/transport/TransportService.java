package com.merlin.transport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.merlin.bean.ClientMeta;
import com.merlin.bean.FMode;
import com.merlin.bean.LocalFile;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class TransportService extends Service  implements Transporter.Callback {
    /**
     * @deprecated
     */
    private final static String LABEL_FILE_META_LIST ="fileMetaList";
    private final static String LABEL_FILE_LIST ="fileList";
    private final static int MODE_UPLOAD =124;
    private final static String LABEL_MODE ="mode";
    private final static String LABEL_FOLDER ="folder";
    private final static String LABEL_DEBUG ="debug";
    private final static String LABEL_COVER_MODE ="coverMode";
    private final static String LABEL_CLIENT ="client";
    private final static String LABEL_INTERACTIVE ="interactive";
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<Transporter.Callback,Long> mCallbacks=new WeakHashMap<>();
    private final Binder mBinder=new Binder();

    private final Uploader.OnStatusChange mStatusChange=(status,transport)-> {
        Map<Transporter.Callback,Long> callbacks= mCallbacks;
        if (null!=callbacks){
            final Handler handler=mHandler;
            synchronized (callbacks){
                Set<Transporter.Callback> set=callbacks.size()>0?callbacks.keySet():null;
                if (null!=set){
                    for (Transporter.Callback callback:set){
                        if (null!=callback&&callback instanceof Uploader.OnStatusChange){
                            handler.post(()->((Uploader.OnStatusChange)callback).onStatusChanged(status,transport));
                        }
                    }
                }
            }
        }
    };
    private final FileDownloader mDownloader=new FileDownloader();
    private final Uploader mFileUploader=new Uploader(){
        @Override
        protected Context getContext() {
            return TransportService.this;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mFileUploader.listener(mStatusChange, Transporter.Callback.TRANSPORT_ADD,"While service create.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=null!=intent?intent.getExtras():null;
        handCommand(bundle);
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean add(Transport transport,String debug){
        if (null!=transport){
            Uploader uploader=mFileUploader;
            if (transport instanceof Upload&&null!=uploader){
                return uploader.upload((Upload)transport,false,null,debug);
            }
        }
        return false;
    }

    private boolean remove(Transport transport,String debug){

        return false;
    }

    private final boolean handCommand(Bundle bundle){
        Object mode=null!=bundle?bundle.get(LABEL_MODE):null;
        if (null==mode){
            return false;
        }
        if (mode.equals(MODE_UPLOAD)){
            Uploader uploader=mFileUploader;
            ArrayList<CharSequence> files=bundle.getCharSequenceArrayList(LABEL_FILE_LIST);
            Object coverMode=bundle.get(LABEL_COVER_MODE);
            Object client=bundle.get(LABEL_CLIENT);
            Object folder=bundle.get(LABEL_FOLDER);
            Object debug=bundle.get(LABEL_DEBUG);
            Object interactive=bundle.get(LABEL_INTERACTIVE);
            return uploader.upload(files,
                    null!=folder&&folder instanceof String?((String)folder):null,
                    null!=interactive&&interactive instanceof Boolean?((Boolean)interactive):true,
                    null!=coverMode&&coverMode instanceof Integer?((Integer)coverMode): FMode.MODE_NONE,
                    null!=client&&client instanceof ClientMeta?((ClientMeta)client):null,mStatusChange,
                    null!=debug&&debug instanceof String?((String)debug):null);
        }
        return false;
    }

    public static boolean upload(Context context, boolean interactive , ArrayList<CharSequence> files, ClientMeta meta, String folder, int mode, String debug){
        if (null!=files&&files.size()>0){
            String url=null!=meta?meta.getUrl():null;
            if (null==url||url.length()<=0){
                Debug.W(TransportService.class,"Can't upload file with invalid server "+(null!=debug?debug:".")+" url="+url);
                return false;
            }
            Debug.D(TransportService.class,"Post upload to service "+url+" "+mode+" "+" "+folder+" "+(null!=debug?debug:"."));
            Intent intent=new Intent(context,TransportService.class);
            intent.putExtra(LABEL_COVER_MODE,mode);
            intent.putExtra(LABEL_CLIENT,meta);
            intent.putExtra(LABEL_MODE,MODE_UPLOAD);
            intent.putExtra(LABEL_INTERACTIVE,interactive);
            intent.putExtra(LABEL_FOLDER,folder);
            intent.putExtra(LABEL_DEBUG,debug);
            intent.putCharSequenceArrayListExtra(LABEL_FILE_LIST,files);
            return null!=context.startService(intent);
        }
        Debug.W(TransportService.class,"Can't upload file with EMPTY list "+(null!=debug?debug:"."));
        return false;
    }

    private class Binder extends android.os.Binder implements TransportBinder {

        @Override
        public Collection<? extends Transport> getRunning(int type) {
            Uploader uploader=(type& Transporter.TYPE_UPLOAD)==Transporter.TYPE_UPLOAD?mFileUploader:null;
            if (null!=uploader){
                return uploader.getUploading(null);
            }
            return null;
        }

        @Override
        public boolean run(int status, Transport transport, String debug) {
            switch (status){
                case TRANSPORT_ADD:
                    return add(transport,debug);
                case TRANSPORT_REMOVE:
                    return remove(transport,debug);
            }
            return false;
        }

        @Override
        public boolean callback(int status,Transporter.Callback callback) {
            Map<Transporter.Callback,Long> reference=null!=callback?mCallbacks:null;
            if (null!=reference){
                synchronized (reference) {
                    return status==Transporter.Callback.TRANSPORT_ADD?!reference.containsKey(callback) &&null == reference.put(callback,
                            System.currentTimeMillis()):null != reference.remove(callback);
                }
            }
            return false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFileUploader.listener(mStatusChange, Transporter.Callback.TRANSPORT_REMOVE,"While service destroy.");
    }
}
