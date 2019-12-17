package com.merlin.task;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.dialog.DownloadFileExistDialog;
import com.merlin.global.Application;

import java.util.ArrayList;
import java.util.List;

public class DownloadService extends Service {
    private static final String LABEL_DOWNLOAD ="download";
    private Client mClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.app.Application app=getApplication();
        Application application=null!=app&&app instanceof Application?(Application)app:null;
        mClient=null!=application?application.getClient():null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Parcelable parcelable=null!=intent?intent.getParcelableExtra(LABEL_DOWNLOAD):null;
        if (null!=parcelable&&parcelable instanceof Download){
            Download download=(Download)parcelable;
            Debug.D(getClass(),"Download task."+download);
            List<Download> list=new ArrayList<Download>(1);
            list.add(download);
            download(list);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean download(List<Download> downloads){
        if ((null!=downloads?downloads.size():0)>0){
            List<Download> exist=new ArrayList<>();
            for (Download download:downloads){
                if (null!=download){
                    exist.add(download);
                }
            }
            if (null!=exist&&exist.size()>0){
                new DownloadFileExistDialog(this).setCallback((what,result)->{
                    switch (what){
                        case DownloadFileExistDialog.Callback.WHAT_KEEP_ALL:

                            break;
                        case DownloadFileExistDialog.Callback.WHAT_REPLACE_ALL:
                            break;
                    }
                }).show("Replace confirm",exist);
            }else{

            }
        }
        return false;
    }

    public static boolean post(Context context,Download download){
        if (null!=context&&null!=download){
            Intent intent=new Intent(context,DownloadService.class);
            intent.putExtra(LABEL_DOWNLOAD,download);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return null!=context.startForegroundService(intent);
            }else{
                return null!=context.startService(intent);
            }
        }
        Debug.W(DownloadService.class,"Can't post download task.download="+download+" context="+context);
        return false;
    }
}
