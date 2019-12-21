package com.merlin.task;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.bean.FileMeta;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
        Debug.D(getClass(),"Download service onCreate.");
        android.app.Application app=getApplication();
        Application application=null!=app&&app instanceof Application?(Application)app:null;
        mClient=null!=application?application.getClient():null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<Parcelable> list=null!=intent?intent.getParcelableArrayListExtra(LABEL_DOWNLOAD):null;
        int size=null!=list?list.size():0;
        if (size>0){
            for (Parcelable parcelable:list){
                if (null==parcelable||!(parcelable instanceof Download)){
                    Debug.W(getClass(),"Skip download task,Invalid."+parcelable);
                    continue;
                }
                download((Download)parcelable);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Client.Canceler download(Download download){
        String from=null!=download?download.getFrom():null;
        String name=null!=download?download.getName():null;
        String srcPath=null!=download?download.getSrc():null;
        if (null==name||name.length()<=0||null==srcPath||srcPath.length()<=0){
            Debug.W(getClass(),"Can't download file,Args invalid."+from+" "+srcPath+" "+name);
            return null;
        }
        String targetFolderPath=download.getTarget();
        targetFolderPath=null==targetFolderPath||targetFolderPath.length()<=0? "/sdcard/a":targetFolderPath;
        if (null==targetFolderPath||targetFolderPath.length()<=0){
            Debug.D(getClass(),"Can't download,Target folder invalid."+targetFolderPath);
            return null;
        }
        File targetFolder=new File(targetFolderPath);
        if (!targetFolder.exists()){
            targetFolder.mkdirs();
        }
        if (!targetFolder.exists()||targetFolder.isFile()){
            Debug.W(getClass(),"Can't download.Target folder create fail or is file."+targetFolder);
            return null;
        }
        File targetFile=new File(targetFolder,name);
        boolean exist=targetFile.exists();
        if (exist&&download.getType()!=Download.TYPE_REPLACE){
            Debug.W(getClass(),"Can't download file.File already existed."+targetFile);
            return null;
        }
        if (!exist){
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                Debug.E(getClass(),"Can't download file.Create file fail.e="+e+" "+targetFile,e);
                e.printStackTrace();
            }finally {
                if (!targetFile.exists()){//Check if create succeed.
                    Debug.W(getClass(),"Can't download file.Create file fail."+targetFile);
                    return null;
                }
            }
        }
        Client client=mClient;
        if (null==client||!client.isLogined()){
            Debug.W(getClass(),"Can't download file.Not login."+client);
            return null;
        }// /volume1/Upload/Videos/Cartoon/Shaun the sheep/Season 1/
        Debug.D(getClass(),"Downloading file."+"\n from:"+srcPath+"\n to:"+targetFile);
        FileOutputStream os=null;
        Client.Canceler canceler=null;
        try {
            final FileOutputStream fos=os=new FileOutputStream(targetFile);
            from="linqiang";
//            srcPath="/volumes/pythonCodes/linqiang.mp3";
            srcPath="/volumes/pythonCodes/1576847957749986.mp4";
//            srcPath="/volumes/pythonCodes/1576846797997566.mp4";
//            srcPath="/volumes/pythonCodes/iPartment.S04E01.HDTV.720p.x264.AAC-sherry.mp4";
            return canceler=client.download(from, srcPath,0,(succeed,what,note,frame)->{
                if (succeed) {
                    byte[] body = null != frame ? frame.getBodyBytes() : null;
                    int length = null != body ? body.length : 0;
                    if (length>0){
                        try {
                            fos.write(body,0,length);
                            if (null!=frame&&frame.isLastFrame()){
                                Debug.D(getClass(),"下载完成了 "+note);
                                 new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent=new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            String type = "video/*";
                                            Uri uri = Uri.parse(targetFile.getAbsolutePath());
                                            Debug.D(getClass(),"下载 结束 "+targetFile);
                                            intent.setDataAndType(uri,type);
                                            try {
                                                DownloadService.this.startActivity(intent);
                                            }catch (Exception e){
                                                Debug.D(getClass(),"eee "+e);
                                            }
                                        }
                                    });
                            }
                        }catch (Exception e){
                            Debug.E(getClass(),"Failed download file.e="+e+" \n"+targetFile,e);
                            closeStream(fos);
                            targetFile.delete();
                        }
                    }
                    Debug.D(getClass(), " " + Thread.currentThread().getName() + " " + what + " " + length);
                }else{
                    Debug.W(getClass(),"Failed download file."+succeed+" "+what+" "+targetFile);
                    targetFile.delete();
                    closeStream(fos);
                }
            });
        } catch (Exception e) {
            Debug.E(getClass(),"Failed download file.e="+e,e);
            targetFile.delete();
        }finally {
            if (null==canceler&&null!=os){
                Debug.E(getClass(),"Close file OutputStream While download fail. "+targetFile);
                closeStream(os);
            }
        }
        //                new Socket.OnRequestFinish() {
//            @Override
//            public void onRequestFinish(boolean succeed, int what, Frame frame) {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        byte[] bytes=frame.getBodyBytes();
//                        Dialog dialog=new Dialog(MM);
//                        ImageView imageView=new ImageView(MM);
//                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
//                        dialog.setContentView(imageView);
//                        dialog.show();
//                    }
//                });
//            }
//        }
        return null;
    }

    private void closeStream(Closeable closeable){
        if (null!=closeable){
            try {
                closeable.close();
            } catch (IOException e) {
                //Do nothing
            }
        }
    }

    private boolean isDownloading(Download download){
        return false;
    }

    private boolean isRunning(Download download){
        return false;
    }

    private boolean download(List<Download> downloads){
        if ((null!=downloads?downloads.size():0)>0){
            List<Download> exist=new ArrayList<>();
            for (Download download:downloads){//Check if exist running
                if (null!=download){
                    if (isRunning(download)){
                        exist.add(download);
                    }else{
//                        /volume1/Upload/Videos/Cartoon/Shaun the sheep/Season 1/
                        Debug.D(getClass(),"下载 "+download);
                    }
                }
            }
//            if (null!=exist&&exist.size()>0){
//                new DownloadFileExistDialog(this).setCallback((what,result)->{
//                    switch (what){
//                        case DownloadFileExistDialog.Callback.WHAT_KEEP_ALL:
//
//                            break;
//                        case DownloadFileExistDialog.Callback.WHAT_REPLACE_ALL:
//                            break;
//                    }
//                }).show("Replace confirm",exist);
//            }else{
//
//            }
        }
        return false;
    }

    public static boolean postDownload(Context context, FileMeta meta,String folder) {
        if (null!=context&&null!=meta){
            List<FileMeta> list=new ArrayList<>(1);
            list.add(meta);
            return postDownload(context,list,folder);
        }
        return false;
    }

    public static boolean postDownload(Context context, List<FileMeta> download,String folder) {
        int size=null!=download?download.size():0;
        if (size>0){
            if (null!=folder&&new File(folder).isFile()){
                Debug.W(DownloadService.class,"Can't post download,folder is file."+folder);
                return false;
            }
            String unique=Long.toString(System.currentTimeMillis());
            List<Download> downloads=new ArrayList<>(size);
            for (FileMeta meta:download){
                if (null!=meta){
                    String from="nas";//test
                    downloads.add(new Download(from,meta.getFile(),meta.getName(),folder,unique));
                }
            }
            return null!=downloads&&downloads.size()>0&&postDownload(context,downloads);
        }
        return false;
    }

    public static boolean postDownload(Context context, Collection<Download> download){
        if (null!=context&&null!=download){
            Intent intent=new Intent(context,DownloadService.class);
            if (!(download instanceof ArrayList)){
                List<Download> list=new ArrayList<>();
                list.addAll(download);
                download=list;
            }
            intent.putParcelableArrayListExtra(LABEL_DOWNLOAD,(ArrayList<Download>) download);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return null!=context.startForegroundService(intent);
            }else{
                return null!=context.startService(intent);
            }
        }
        Debug.W(DownloadService.class,"Can't post download task.download="+download+" context="+context);
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"Download service onDestroy.");
    }
}
