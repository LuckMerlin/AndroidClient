package com.merlin.task;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadService extends Service {
    private static final String LABEL_DOWNLOAD ="download";
    private final List<DownloadTask> mRunningList=new ArrayList<>();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final List<Download> mWaiting=new ArrayList<>();
    private final Map<DownloadTask, Client.Canceler> mDownloading=new HashMap<>();
    private WeakReference<Callback> mCallback;
    private int mMaxDownloading;
    private Client mClient;
    private final Binder mBinder=new Binder();

    public interface Callback{
        int FINISH_SERVICE_FAIL=999;
        int FINISH_START_FAIL=1000;
        int FINISH_EXCEPTION=1001;
        int FINISH_WRITE_EXCEPTION=1002;
        int FINISH_SUCCEED=1003;
        int FINISH_CANCEL=1004;
        int START=1005;
        int PROGRESS=1006;
        int PAUSE=1007;
        int RESTART=1008;

        void onFileDownloadUpdate(int what,boolean finish,DownloadTask task,Object data);
    }

    public class Binder extends android.os.Binder implements Downloader{
       public  List<DownloadTask>  getDownloadList(){
               synchronized (mRunningList) {
                   List<DownloadTask> list = new ArrayList<>(mRunningList.size());
                   list.addAll(mRunningList);
                   return list;
               }
        }

        public Client.Canceler download(Download download){
           return DownloadService.this.download(download);
        }

        public boolean isRunning(Download download){
            synchronized (mRunningList) {
                for (DownloadTask task:mRunningList){
                    Download child=null!=task?task.getDownload():null;
                    if (null!=child&&child.equals(download)){
                        return true;
                    }
                }
            }
           return false;
        }

        public boolean isDownloading(Download download){
            synchronized (mRunningList) {
                for (DownloadTask task:mRunningList){
                    Download child=null!=task?task.getDownload():null;
                    if (null!=child&&child.equals(download)){
                        return task.isDownloading();
                    }
                }
            }
           return false;
        }

        public boolean pause(Download download){
            synchronized (mRunningList) {
                for (DownloadTask task:mRunningList){
                    Download child=null!=task?task.getDownload():null;
                    if (null!=child&&child.equals(download)){
                         return pause(task);
                    }
                }
            }
           return false;
        }

        public boolean cancel(Download download){
            synchronized (mRunningList) {
                for (DownloadTask task:mRunningList){
                    Download child=null!=task?task.getDownload():null;
                    if (null!=child&&child.equals(download)){
                        return cancel(task);
                    }
                }
            }
           return false;
        }

        public boolean pause(DownloadTask task){
            Map<DownloadTask, Client.Canceler> downloading=mDownloading;
            if (null!=task&&null!=downloading){
                synchronized (downloading){
                    Client.Canceler canceler=downloading.get(task);
                    return null!=canceler&&canceler.cancel(true);
                }
            }
            return false;
        }

        public boolean cancel(DownloadTask task){
            if (null!=task){
                task.setDeleteIncomplete(true);
                return pause(task);
            }
           return false;
        }

        public void setCallback(Callback callback){
            WeakReference<Callback> reference=mCallback;
            if (null!=reference){
                reference.clear();
                mCallback=null;
            }
            if (null!=callback){
                mCallback=new WeakReference<>(callback);
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
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

    public void onFileDownloadUpdate(int what,boolean finish,DownloadTask task,Object data){
        WeakReference<Callback> reference=mCallback;
        Callback callback=null!=reference?reference.get():null;
        Handler handler=mHandler;
        if (null!=callback&&null!=handler){
            handler.post(()->callback.onFileDownloadUpdate(what,finish,task,data));
        }
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
        }
        final List<DownloadTask> runningList=mRunningList;
        final Map<DownloadTask, Client.Canceler> downloading=mDownloading;
        if (null==runningList){
            Debug.W(getClass(),"Can't download file.runningList="+runningList+" "+download);
            return null;
        }
        if (mBinder.isRunning(download)){
            Debug.W(getClass(),"Can't download file.Exist downloading."+download);
            return null;
        }
        // /volume1/Upload/Videos/Cartoon/Shaun the sheep/Season 1/
        Debug.D(getClass(),"Downloading file."+"\n from:"+srcPath+"\n to:"+targetFile);
        FileOutputStream os=null;
        Client.Canceler canceler=null;
        final DownloadTask task=new DownloadTask(download);
        try {
            final FileOutputStream fos=os=new FileOutputStream(targetFile);
            from="linqiang";
//            srcPath="/volumes/pythonCodes/linqiang.mp3";
            srcPath="/volumes/pythonCodes/1576847957749986.mp4";
//            srcPath="/volumes/pythonCodes/1576846797997566.mp4";
//            srcPath="/volumes/pythonCodes/iPartment.S04E01.HDTV.720p.x264.AAC-sherry.mp4";
            long startTime=System.currentTimeMillis();
            task.setStartTime(startTime);
            runningList.add(task);
            onFileDownloadUpdate(Callback.START,false,task,startTime);
            final long[] total=new long[1];
            canceler=client.download(from, srcPath,0,(succeed,what,note,frame)->{
                if (succeed) {
                    byte[] body = null != frame ? frame.getBodyBytes() : null;
                    int length = null != body ? body.length : 0;
                    if (length>0){
                        try {
                            fos.write(body,0,length);
                            long remain=frame.getRemain();
                            if (remain>=0) {
                                if (total[0] <= 0) {//If total not set
                                    if (remain > 0) {
                                        task.setTotal(total[0] = (remain + length));
                                    }
                                }
                                task.setRemain(remain);
                                onFileDownloadUpdate(Callback.PROGRESS,false,task,remain);
                            }
                            if (null!=frame&&frame.isLastFrame()){
                                runningList.remove(task);
                                downloading.remove(task);
                                closeStream(fos);
                                onFileDownloadUpdate(Callback.FINISH_SUCCEED,true,task,System.currentTimeMillis());
                                Debug.D(getClass(),"下载完成了 "+note);
//                                 new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Intent intent=new Intent();
//                                            intent.setAction(Intent.ACTION_VIEW);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            String type = "video/*";
//                                            Uri uri = Uri.parse(targetFile.getAbsolutePath());
//                                            Debug.D(getClass(),"下载 结束 "+targetFile);
//                                            intent.setDataAndType(uri,type);
//                                            try {
//                                                DownloadService.this.startActivity(intent);
//                                            }catch (Exception e){
//                                                Debug.D(getClass(),"eee "+e);
//                                            }
//                                        }
//                                    });
                            }
                        }catch (Exception e){
                            Debug.E(getClass(),"Failed download file.e="+e+" \n"+targetFile,e);
                            closeStream(fos);
                            targetFile.delete();
                            runningList.remove(task);
                            downloading.remove(task);
                            if (task.isDeleteIncomplete()){
                                targetFile.delete();
                            }
                            onFileDownloadUpdate(Callback.FINISH_WRITE_EXCEPTION,true,task,e);
                        }
                    }
                    Debug.D(getClass(), " " + Thread.currentThread().getName() + " " + what + " " + length);
                }else{
                    Debug.W(getClass(),"Failed download file."+succeed+" "+what+" "+targetFile);
                    targetFile.delete();
                    closeStream(fos);
                    downloading.remove(task);
                    runningList.remove(task);
                    if (task.isDeleteIncomplete()){
                        targetFile.delete();
                    }
                    onFileDownloadUpdate(Callback.FINISH_SERVICE_FAIL,true,task,null);
                }
            });
            if (null!=canceler){
                downloading.put(task,canceler);
            }
            return canceler;
        } catch (Exception e) {
            Debug.E(getClass(),"Failed download file.e="+e,e);
            targetFile.delete();
        }finally {
            if (null==canceler&&null!=os){
                Debug.E(getClass(),"Close file OutputStream While download fail. "+targetFile);
                closeStream(os);
                downloading.remove(task);
                runningList.remove(task);
                if (task.isDeleteIncomplete()){
                    targetFile.delete();
                }
                onFileDownloadUpdate(Callback.FINISH_START_FAIL,true,task,null);
            }
        }
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
