package com.merlin.transport;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;

import java.util.ArrayList;

public class TransportService extends Service {
    private final FileDownloader mDownloader=new FileDownloader();
    private final static String LABEL_FILE_META_LIST ="fileMetaList";
    private final static int MODE_INVALID =-1;
    private final static int MODE_DOWNLOAD =123;
    private final static String LABEL_MODE ="mode";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null!=intent){
            ArrayList<NasFile> list=intent.getParcelableArrayListExtra(LABEL_FILE_META_LIST);
            if (null!=list&&list.size()>0){
                onFileMetaListReceived(intent,list,"Receive from intent.");
            }
        }
        return super.onStartCommand(intent, flags, startId);
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

}
