package com.merlin.transport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import com.merlin.api.Client;
import com.merlin.api.Label;
import com.merlin.debug.Debug;
import com.merlin.global.Service;
import com.merlin.server.Retrofit;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConveyorService extends Service implements Label,OnConveyStatusChange{
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
        handCommand(null!=intent?intent.getExtras():null);
        return super.onStartCommand(intent, flags, startId);
    }

    private final boolean handCommand(Bundle bundle) {
        final Object what = null != bundle ? bundle.get(LABEL_WHAT) : null;
        final Conveyor conveyor=mConveyor;
        if (null == what) {
            return false;
        }
        if (what.equals(LABEL_UPLOAD)) {
            String folder = bundle.getString(LABEL_FOLDER);
            Object client = bundle.get(LABEL_CLIENT);
            String url=null!=client&&client instanceof Client?((Client)client).getUrl():null;
            if (null==url||url.length()<=0){
                return false;
            }
            Object debugObj = bundle.get(LABEL_HINT);
            String debug=null!=debugObj&&debugObj instanceof String?(String)debugObj:null;
            Serializable filesObject = bundle.getSerializable(LABEL_DATA);
            List<File> files=null!=filesObject&&filesObject instanceof ArrayList?(List<File>)filesObject:null;
            if (null!=files&&files.size()>0){
                for (File file:files){
                    conveyor.add(null, debug,new FileUploadConvey(mRetrofit,file,url,folder));
                }
                return true;
            }
            return false;
        } else if (what.equals(LABEL_UPLOAD)) {
//            String filePath = bundle.getString(LABEL_FILES);
//            int coverMode = bundle.getInt(LABEL_COVER_MODE, CoverMode.COVER_MODE_NONE);
//            Object client = bundle.get(LABEL_CLIENT);
//            String folder = bundle.getString(LABEL_FOLDER);
//            String debug = bundle.getString(LABEL_DEBUG, null);
//            boolean interactive = bundle.getBoolean(LABEL_INTERACTIVE, false);
//            return download(filePath, folder, interactive, coverMode, null != client && client instanceof ClientMeta ? ((ClientMeta) client) : null, debug);
        }
        return false;
    }

    public static boolean upload(Context context, File file, Client meta, String folder, int mode, String debug) {
        if (null!=file&&null!=context){
            ArrayList<File> list=new ArrayList<>();
            list.add(file);
            return upload(context,list,meta,folder,mode,debug);
        }
        Debug.W(ConveyorService.class,"Can't upload file with NULL "+(null!=debug?debug:"."));
        return false;
    }

    public static boolean upload(Context context, ArrayList<File> files, Client meta, String folder, int mode, String debug){
        if (null!=files&&files.size()>0&&null!=context){
            final String url=null!=meta?meta.getUrl():null;
            if (null==url||url.length()<=0){
                Debug.W(ConveyorService.class,"Can't upload file with invalid server "+(null!=debug?debug:".")+" url="+url);
                return false;
            }
            Debug.D(ConveyorService.class,"Post upload to service "+url+" "+mode+" "+" "+folder+" "+(null!=debug?debug:"."));
            Intent intent=new Intent(context,ConveyorService.class);
            intent.putExtra(LABEL_DATA,files);
            intent.putExtra(LABEL_CLIENT,meta);
            intent.putExtra(LABEL_HINT,null!=debug?debug:"");
            intent.putExtra(LABEL_WHAT,LABEL_UPLOAD);
            intent.putExtra(LABEL_FOLDER,folder);
            return null!=context.startService(intent);
        }
        Debug.W(ConveyorService.class,"Can't upload file with EMPTY list "+(null!=debug?debug:"."));
        return false;
    }

    private class Binder extends android.os.Binder implements ConveyorBinder {
        @Override
        public Collection<Convey> get(Class<? extends Convey> cls, int... status) {
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
