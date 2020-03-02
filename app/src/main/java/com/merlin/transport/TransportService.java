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

public class TransportService extends Service {
    private final FileDownloader mDownloader=new FileDownloader();
    private final Uploader mFileUploader=new Uploader(){
        @Override
        protected Context getContext() {
            return TransportService.this;
        }
    };
    /**
     * @deprecated
     */
    private final static String LABEL_FILE_META_LIST ="fileMetaList";
    private final static String LABEL_FILE_LIST ="fileList";
    private final static int MODE_INVALID =-1;
    private final static int MODE_DOWNLOAD =123;
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

    private final Uploader.OnUploadProgress mUploadProgress=(from,folder, name, upload, total)-> {
        Map<Transporter.Callback,Long> callbacks= mCallbacks;
        if (null!=callbacks){
            synchronized (callbacks){
                Set<Transporter.Callback> set=callbacks.size()>0?callbacks.keySet():null;
                if (null!=set){
                    for (Transporter.Callback callback:set){
                        if (null!=callback&&callback instanceof Uploader.OnUploadProgress){
                            ((Uploader.OnUploadProgress)callback).onUploadProgress();
                        }
                    }
                }
            }
        }
    };

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
                    null!=interactive&&interactive instanceof Boolean?((Boolean)interactive):true,
                    null!=coverMode&&coverMode instanceof Integer?((Integer)coverMode): FMode.MODE_NONE,
                    null!=client&&client instanceof ClientMeta?((ClientMeta)client):null,
                    null!=folder&&folder instanceof String?((String)folder):null,mUploadProgress,
                    null!=debug&&debug instanceof String?((String)debug):null);
        }
        return false;
    }

    private boolean onFileMetaListReceived(Intent intent, ArrayList<NasFile> list, String debug){
        if (null!=list&&list.size()>0){
            switch (null!=intent?intent.getIntExtra(LABEL_MODE,MODE_INVALID):MODE_INVALID){
                case MODE_DOWNLOAD:
                    FileDownloader downloader=mDownloader;
                    return null!=downloader&&downloader.download(list,"/sdcard/a",debug);
            }
        }
        return false;
    }

    public static boolean download(Context context, Parcelable file, String debug){
           if (null!=context&&null!=file){
               ArrayList<Parcelable> list=new ArrayList<>(1);
//               list.add(file);
               Toast.makeText(context,"下载 "+file,Toast.LENGTH_SHORT).show();
               return download(context,list,debug);
           }
            return false;
    }

    public static boolean download(Context context, ArrayList<Parcelable> list, String debug){
        final int size=null!=list&&null!=context?list.size():-1;
        if (size>0){
            Debug.D(TransportService.class,"Post download to service "+size+" "+(null!=debug?debug:"."));
            Intent intent=new Intent(context,TransportService.class);
            intent.putParcelableArrayListExtra(LABEL_FILE_META_LIST,list);
            intent.putExtra(LABEL_MODE,MODE_DOWNLOAD);
            context.startService(intent);
            return true;
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
        public boolean add(Transporter.Callback progress) {
            Map<Transporter.Callback,Long> reference=null!=progress?mCallbacks:null;
            if (null!=reference){
                synchronized (reference) {
                    return !reference.containsKey(progress) && null == reference.put(progress, System.currentTimeMillis());
                }
            }
            return false;
        }

        @Override
        public boolean remove(Transporter.Callback progress) {
            Map<Transporter.Callback,Long> reference=null!=progress?mCallbacks:null;
            if (null!=reference){
                synchronized (reference) {
                    return null != reference.remove(progress);
                }
            }
            return false;
        }
    }

}
