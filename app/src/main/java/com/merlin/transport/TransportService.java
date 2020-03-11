package com.merlin.transport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.merlin.bean.ClientMeta;
import com.merlin.debug.Debug;
import com.merlin.file.CoverMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class TransportService extends Service  implements Callback {
    /**
     * @deprecated
     */
    private final static String LABEL_FILE_META_LIST ="fileMetaList";
    private final static String LABEL_FILES ="filePaths";
    private final static int MODE_UPLOAD =124;
    private final static int MODE_DOWNLOAD =125;
    private final static String LABEL_MODE ="mode";
    private final static String LABEL_FOLDER ="folder";
    private final static String LABEL_DEBUG ="debug";
    private final static String LABEL_COVER_MODE ="coverMode";
    private final static String LABEL_CLIENT ="client";
    private final static String LABEL_INTERACTIVE ="interactive";
    private final static String LABEL_NAME ="name";
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<Callback,Long> mCallbacks=new WeakHashMap<>();
    private final Binder mBinder=new Binder();
    private final Transporter mTransporter=new Transporter(this,mHandler);
    private final OnStatusChange mOnStatusChange=(int status, AbsTransport transport, Object data)->{
        Map<Callback,Long> callbacks= mCallbacks;
        if (null!=callbacks){
            final Handler handler=mHandler;
            synchronized (callbacks){
                Set<Callback> set=callbacks.size()>0?callbacks.keySet():null;
                if (null!=set){
                    for (Callback callback:set){
                        if (null!=callback&&callback instanceof OnStatusChange){
                            handler.post(()->((OnStatusChange)callback).onStatusChanged(status,transport,data));
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mTransporter.listener(mOnStatusChange, TRANSPORT_ADD,"While service create.");
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

//    private boolean add(AbsTransport transport, boolean interactive, String debug){
//        Transporter transporter=null!=transport?mTransporter:null;
//        return null!=transporter&&transporter.add(transport,interactive,null,debug);
//    }
//
//    private boolean remove(AbsTransport transport, String debug){
//        Transporter transporter=null!=transport?mTransporter:null;
//        return null!=transporter&&transporter.add(transport,interactive,null,debug);
//    }

    private boolean upload(String filePath,String folder,String name,boolean interactive,int coverMode,ClientMeta client,String debug){
        if (null!=filePath&&filePath.length()>0&&null!=client){
            return run(TRANSPORT_ADD,interactive,debug,new Upload(filePath,folder,name,client,coverMode));
        }
        return false;
    }

    private boolean download(ArrayList<CharSequence> paths,String folder,boolean interactive,int coverMode,ClientMeta client,String debug){
        if (null!=paths&&paths.size()>0){

        }
        return false;
    }

    private final boolean handCommand(Bundle bundle){
        Object mode=null!=bundle?bundle.get(LABEL_MODE):null;
        if (null==mode||!(mode instanceof Integer)){
            return false;
        }
        if (mode.equals(MODE_UPLOAD)){
            String filePath=bundle.getString(LABEL_FILES);
            int coverMode=bundle.getInt(LABEL_COVER_MODE,CoverMode.COVER_MODE_NONE);
            Object client=bundle.get(LABEL_CLIENT);
            String folder=bundle.getString(LABEL_FOLDER);
            String name=bundle.getString(LABEL_NAME);
            String debug=bundle.getString(LABEL_DEBUG,null);
            boolean interactive=bundle.getBoolean(LABEL_INTERACTIVE,false);
            return upload(filePath,folder,name,interactive,coverMode, null!=client&&client instanceof ClientMeta?((ClientMeta)client):null,debug);
        }else if (mode.equals(MODE_DOWNLOAD)){
            String filePath=bundle.getString(LABEL_FILES);
            int coverMode=bundle.getInt(LABEL_COVER_MODE,CoverMode.COVER_MODE_NONE);
            Object client=bundle.get(LABEL_CLIENT);
            String folder=bundle.getString(LABEL_FOLDER);
            String debug=bundle.getString(LABEL_DEBUG,null);
            boolean interactive=bundle.getBoolean(LABEL_INTERACTIVE,false);
//            return download(files,folder,interactive,coverMode, null!=client&&client instanceof ClientMeta?((ClientMeta)client):null,debug);
            return false;
        }
        return false;
    }

    private final boolean run(int status,  boolean interactive, String debug, AbsTransport... transports) {
        Transporter transporter=null!=transports&&transports.length>0?mTransporter:null;
        return null!=transporter&&transporter.run(status,interactive,debug,transports);
    }

    private class Binder extends android.os.Binder implements TransportBinder {

        @Override
        public Collection<? extends AbsTransport> getRunning(int type) {
            Transporter uploader=mTransporter;
            if (null!=uploader){
                return uploader.getTransporting(null);
            }
            return null;
        }

        @Override
        public boolean run(int status, boolean interactive, String debug, AbsTransport... transports) {
            return TransportService.this.run(status,interactive,debug,transports);
        }

        @Override
        public boolean callback(int status,Callback callback) {
            Map<Callback,Long> reference=null!=callback?mCallbacks:null;
            if (null!=reference){
                synchronized (reference) {
                    return status==TRANSPORT_ADD?!reference.containsKey(callback) &&null == reference.put(callback,
                            System.currentTimeMillis()):null != reference.remove(callback);
                }
            }
            return false;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTransporter.listener(mOnStatusChange, TRANSPORT_REMOVE,"While service destroy.");
    }

    public static boolean upload(Context context, boolean interactive , CharSequence path, ClientMeta meta, String folder, String name,int mode, String debug){
        if (null!=path&&path.length()>0){
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
            intent.putExtra(LABEL_NAME,name);
            intent.putExtra(LABEL_FOLDER,folder);
            intent.putExtra(LABEL_DEBUG,debug);
            intent.putExtra(LABEL_FILES,path);
            return null!=context.startService(intent);
        }
        Debug.W(TransportService.class,"Can't upload file with EMPTY list "+(null!=debug?debug:"."));
        return false;
    }

}
