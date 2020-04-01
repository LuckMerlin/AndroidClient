package com.merlin.conveyor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import com.merlin.api.CoverMode;
import com.merlin.api.Label;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.LocalFile;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConveyorService extends Service implements Label, OnConveyStatusChange {
    private final Conveyor mConveyor=new Conveyor(Looper.getMainLooper());
    private final Retrofit mRetrofit=new Retrofit();
    private final Binder mBinder=new Binder();

    @Override
    public void onCreate() {
        super.onCreate();
        mConveyor.listener(ConveyStatus.ADD,this,"While conveyor service onCreate.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConveyStatusChanged(int status, Convey convey, Object data) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handCommand(null != intent ? intent.getExtras() : null);
        return super.onStartCommand(intent, flags, startId);
    }

    private final boolean handCommand(Bundle bundle) {
        final Object what = null != bundle ? bundle.get(LABEL_WHAT) : null;
        final Conveyor conveyor=mConveyor;
        if (null == what||null==conveyor) {
            Debug.W(getClass(),"Can't handle convey intent "+what+" "+conveyor);
            return false;
        }
        Object debugObj = bundle.get(LABEL_HINT);
        String debug=null!=debugObj&&debugObj instanceof String?(String)debugObj:null;
        if (what.equals(LABEL_UPLOAD)) {
            String folder = bundle.getString(LABEL_FOLDER);
            Object client = bundle.get(LABEL_CLIENT);
            String url=null!=client&&client instanceof ClientMeta?((ClientMeta)client).getUrl():null;
            if (null==url||url.length()<=0){
                Debug.D(CoverMode.class,"Can't handle upload intent which client url is NONE.");
                return false;
            }
            Serializable filesObject = bundle.getSerializable(LABEL_DATA);
            ArrayList list=null!=filesObject&&filesObject instanceof ArrayList?(ArrayList)filesObject:null;
            if (null!=list){
                for (Object file:list){
                    String path=null!=file&&file instanceof LocalFile?((LocalFile)file).getPath():null;
                    File child=null!=path&&path.length()>0?new File(path):null;
                    if (null!=child&&child.exists()){
                        conveyor.add(null, debug,new FileUploadConvey(mRetrofit,child,url,folder));
                    }
                }
                return true;
            }
            return false;
        } else if (what.equals(LABEL_DOWNLOAD)) {
            String folder = bundle.getString(LABEL_FOLDER);
            Object client = bundle.get(LABEL_CLIENT);
            String url=null!=client&&client instanceof ClientMeta?((ClientMeta)client).getUrl():null;
            if (null==url||url.length()<=0){
                Debug.D(CoverMode.class,"Can't handle download intent which client url is NULL.");
                return false;
            }else if (null==folder||folder.length()<=0){
                Debug.D(CoverMode.class,"Can't handle download intent which folder is NULL.");
                return false;
            }
            Serializable filesObject = bundle.getSerializable(LABEL_DATA);
            ArrayList list=null!=filesObject&&filesObject instanceof ArrayList?(ArrayList)filesObject:null;
            if (null!=list){
                File folderFile=new File(folder);
                for (Object file:list){
                    if (null!=file&&file instanceof NasFile){
                        conveyor.add(null, debug,new FileDownloadConvey(mRetrofit,(NasFile)file,url, folderFile));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean upload(Context context, LocalFile file, ClientMeta meta, String folder,
                                 CoverMode coverMode, String debug) {
        if (null!=file&&null!=context){
            ArrayList<LocalFile> list=new ArrayList<>();
            list.add(file);
            return upload(context,list,meta,folder,coverMode,debug);
        }
        Debug.W(ConveyorService.class,"Can't upload file with NULL "+(null!=debug?debug:"."));
        return false;
    }

    public static boolean download(Context context, ArrayList<NasFile> files, ClientMeta meta, String folder,CoverMode coverMode, String debug) {
        return conveyFiles(context,Label.LABEL_DOWNLOAD,files,meta,folder,coverMode,debug);
    }

    public static boolean upload(Context context, ArrayList<LocalFile> files, ClientMeta meta, String folder, CoverMode coverMode, String debug){
        return conveyFiles(context,Label.LABEL_UPLOAD,files,meta,folder,coverMode,debug);
    }

    private static boolean conveyFiles(Context context, String what,  ArrayList files, ClientMeta meta, String folder, CoverMode coverMode, String debug){
        if (null!=files&&files.size()>0&&null!=context){
            if (null==what||what.length()<=0){
                Debug.W(ConveyorService.class,"Can't convey files with invalid what "+(null!=debug?debug:".")+" what="+what);
                return false;
            }
            final String url=null!=meta?meta.getUrl():null;
            if (null==url||url.length()<=0){
                Debug.W(ConveyorService.class,"Can't convey files with invalid server "+(null!=debug?debug:".")+" url="+url);
                return false;
            }
            Debug.D(ConveyorService.class,"Post convey files to service "+url+" "+coverMode+" "+" "+folder+" "+(null!=debug?debug:"."));
            Intent intent=new Intent(context,ConveyorService.class);
            intent.putExtra(LABEL_DATA,files);
            intent.putExtra(LABEL_CLIENT,meta);
            intent.putExtra(LABEL_MODE,coverMode);
            intent.putExtra(LABEL_HINT,null!=debug?debug:"");
            intent.putExtra(LABEL_WHAT,what);
            intent.putExtra(LABEL_FOLDER,folder);
            return null!=context.startService(intent);
        }
        Debug.W(ConveyorService.class,"Can't convey files with EMPTY list "+(null!=debug?debug:"."));
        return false;
    }

    private class Binder extends android.os.Binder implements ConveyorBinder {
        @Override
        public List<Convey> get(Class<? extends Convey> cls, int... status) {
            Conveyor conveyor=mConveyor;
            return null!=conveyor?conveyor.get(cls,status):null;
        }

        @Override
        public boolean listener(int status, OnConveyStatusChange listener, String debug) {
            Conveyor conveyor=null!=listener?mConveyor:null;
            return null!=conveyor&&conveyor.listener(status,listener,debug);
        }

        @Override
        public boolean run(int status, String debug, Convey... conveys) {
            Conveyor conveyor=null!=conveys&&conveys.length>0?mConveyor:null;
            return null!=conveyor&&conveyor.convey(status,null,debug,conveys);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mConveyor.listener(ConveyStatus.CANCELED,this,"While conveyor service onDestroy.");
    }
}
