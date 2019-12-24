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
import com.merlin.breakpoint.BreakPoint;
import com.merlin.breakpoint.BreakPointer;
import com.merlin.breakpoint.ShBreakPointer;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.protocol.What;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadService extends Service {
    private static final String LABEL_DOWNLOAD ="download";
    private final List<DownloadTask> mRunningList=new ArrayList<>();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<DownloadTask, Client.Canceler> mDownloading=new HashMap<>();
    private WeakReference<Callback> mCallback;
    private BreakPointer mBreakPointer;
    private int mMaxDownloading=1;
    private Client mClient;
    private final Binder mBinder=new Binder();

    public interface Callback extends Status{
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

        public int getDownloadingSize(){
            Map<DownloadTask, Client.Canceler> downloading=mDownloading;
            if (null!=downloading){
                synchronized (downloading){
                    return downloading.size();
                }
            }
           return -1;
        }

        public Client.Canceler download(Download download){
           return null!=download?DownloadService.this.download(download):null;
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
                Download download=null!=task?task.getDownload():null;
                if (null!=download){
                    download.setDeleteIncomplete(true);
                }
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
        BreakPointer pointer=mBreakPointer=new ShBreakPointer(app);
        List<BreakPoint> list=pointer.getBreakpoints();//Init break list
        if (null!=list&&list.size()>0){
            for (BreakPoint point:list){
                DownloadTask task=null!=point?point.getTask():null;
                if (null!=task){ //Add task into queue
                    task.setStatus(Status.WAITING);
                    task.buildRemainFromFile();
                    mRunningList.add(task);
                    onFileDownloadUpdate(Status.WAITING,false,task,null);
                }
            }
            checkDownloadNextPossible();//Check
        }
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

    private Client.Canceler download(final Download download){
        String fromAccount=null!=download?download.getFromAccount():null;
        String name=null!=download?download.getName():null;
        String srcPath=null!=download?download.getSrc():null;
        if (null==name||name.length()<=0||null==srcPath||srcPath.length()<=0){
            Debug.W(getClass(),"Can't download file,Args invalid."+fromAccount+" "+srcPath+" "+name);
            return null;
        }
        String targetFolderPath=download.getTargetFolder();
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
        if (exist&&targetFile.isDirectory()){
            Debug.W(getClass(),"Can't download file.File already existed."+targetFile);
            return null;
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
        final Binder binder=mBinder;
        int index=runningList.indexOf(download);
        DownloadTask existTask;
        synchronized (runningList) {
            existTask = index >= 0 ? runningList.get(index) : null;
        }
        if (null!=existTask&&existTask.getStatus()!=Status.WAITING){
            Debug.W(getClass(),"Can't download file.Exist downloading."+download);
            return null;
        }
        final DownloadTask task=new DownloadTask(download);
        task.setTargetPath(targetFile.getAbsolutePath());
        task.setStatus(Status.UNKNOWN);
        int downloadingSize=binder.getDownloadingSize();
        if (downloadingSize>0){//Check if need waiting
            int maxDownloading=mMaxDownloading;
            if (downloadingSize>(maxDownloading<=0?1:maxDownloading)){
                Debug.W(getClass(),"Add download into wait queue."+downloadingSize+" "+maxDownloading);
                task.setStatus(Status.WAITING);
                onFileDownloadUpdate(Callback.WAITING,true,task,System.currentTimeMillis());
                return null;
            }
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
                    task.setStatus(Status.FINISH_WRITE_EXCEPTION);
                    return null;
                }
            }
        }
        final long seek=download.getType()==Download.TYPE_REPLACE?0:targetFile.length();
        Debug.D(getClass(),"Downloading file."+download.getType()+" "+seek+" \n from:"+srcPath+"\n to:"+targetFile);
        FileOutputStream os=null;
        Client.Canceler canceler=null;
        try {
            final FileOutputStream fos=os=new FileOutputStream(targetFile,seek<=0?false:true);
            long startTime=System.currentTimeMillis();
            task.setStartTime(startTime);
            runningList.add(task);
            onFileDownloadUpdate(Callback.START,false,task,startTime);
            final long[] total=new long[1];
            task.setStatus(Status.DOWNLOADING);
            canceler=client.download(fromAccount, srcPath,seek<=0?0:seek,(succeed,what,note,frame)->{
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
                                onFileDownloadUpdate(Callback.DOWNLOADING,false,task,remain);
                            }
                            if (null!=frame&&frame.isLastFrame()){
                                removeTask(task);
                                closeStream(fos);
                                task.setStatus(Status.FINISH_SUCCEED);
                                mBreakPointer.removeBreakpoint(task);
                                onFileDownloadUpdate(Callback.FINISH_SUCCEED,true,task,System.currentTimeMillis());
                                Debug.D(getClass(),"下载完成了 "+note);
                                checkDownloadNextPossible();
                            }
                        }catch (Exception e){
                            Debug.E(getClass(),"Failed download file.e="+e+" \n"+targetFile,e);
                            closeStream(fos);
                            targetFile.delete();
                            removeTask(task);
                            if (download.isDeleteIncomplete()){
                                targetFile.delete();
                            }else{
                                mBreakPointer.addBreakpoint(new BreakPoint(task));
                            }
                            task.setStatus(Status.FINISH_WRITE_EXCEPTION);
                            onFileDownloadUpdate(Callback.FINISH_WRITE_EXCEPTION,true,task,e);
                        }
                    }
                    Debug.D(getClass(), " " + Thread.currentThread().getName() + " " + what + " " + length);
                }else{
                    boolean alreadyDownloaded=What.WHAT_OUT_OF_BOUNDS == what;
                    boolean canceled=What.WHAT_CANCEL==what;
                    if (alreadyDownloaded){
                        Debug.W(getClass(),"Already downloaded."+targetFile);
                    }else {
                        Debug.W(getClass(), (canceled ? "Canceled" : "Failed") + " download file. " + what + " " + targetFile);
                    }
                    closeStream(fos);
                    removeTask(task);
                    if (alreadyDownloaded){
                        task.setStatus(Status.FINISH_SUCCEED);
                        onFileDownloadUpdate(Callback.FINISH_SUCCEED,true,task,null);
                    }else{
                        if (download.isDeleteIncomplete()){
                            targetFile.delete();
                            mBreakPointer.removeBreakpoint(task);
                        }else{
                            mBreakPointer.addBreakpoint(new BreakPoint(task));
                        }
                        task.setStatus(Status.FINISH_SERVICE_FAIL);
                        onFileDownloadUpdate(Callback.FINISH_SERVICE_FAIL,true,task,null);
                    }
                }
            });
            if (null!=canceler){
                downloading.put(task,canceler);
            }else{
                task.setStatus(Status.FINISH_START_FAIL);
            }
            return canceler;
        } catch (Exception e) {
            Debug.E(getClass(),"Failed download file.e="+e,e);
            targetFile.delete();
        }finally {
            if (null==canceler&&null!=os){
                Debug.E(getClass(),"Close file OutputStream While download fail. "+targetFile);
                closeStream(os);
                task.setStatus(Status.FINISH_START_FAIL);
                removeTask(task);
                if (download.isDeleteIncomplete()){
                    targetFile.delete();
                    mBreakPointer.removeBreakpoint(task);
                }
                onFileDownloadUpdate(Callback.FINISH_START_FAIL,true,task,null);
            }
        }
        return null;
    }

    private boolean removeTask(DownloadTask task){
        Map<DownloadTask, Client.Canceler> downloading=mDownloading;
        if (null!=downloading){
            Set<DownloadTask> set=null;
            synchronized (downloading){
               set=downloading.keySet();
            }
            if (null!=set){
                removeTask(set,task);
            }
        }
        List<DownloadTask> running=mRunningList;
        if (null!=running){
            removeTask(running,task);
        }
        return true;
    }

    private boolean removeTask(Collection<DownloadTask> collection,DownloadTask task){
        if (null!=collection&&null!=task){
            synchronized (collection){
            for (DownloadTask dt:collection){
                if (null!=dt&&dt.equals(task)){
                    collection.remove(dt);
                    break;
                }
             }
            }
        }
        return false;
    }

    private boolean checkDownloadNextPossible(){
        mHandler.post(()->{
          List<DownloadTask> running=mRunningList;
          if (null!=running){
              DownloadTask next = null;
              synchronized (running) {
                  for (DownloadTask task : running) {
                      int status = null != task ? task.getStatus() : Status.INVALID;
                      if (status == Status.WAITING) {
                          next = task;
                          break;
                      }
                  }
              }
              Map downloading=mDownloading;
              if (null!=next&&null!=downloading){
                  int size;
                  synchronized (downloading){
                      size=downloading.size();
                  }
                  int maxSize=mMaxDownloading;
                  if (size<(maxSize<=0?1:maxSize)){
                      Debug.D(getClass(),"Now!Download next file."+next.getTargetPath());
                      download(next.getDownload());
                  }
              }
          }
        });
        return false;
    }

    private void closeStream(FileOutputStream closeable){
        if (null!=closeable){
            try {
                closeable.flush();
            }catch (Exception e){
                //Do nothing
            }
            try {
                closeable.close();
            } catch (IOException e) {
                //Do nothing
            }
        }
    }

    public static boolean postDownload(Context context,String fromAccount,String folder, FileMeta meta) {
        if (null!=context&&null!=meta){
            List<FileMeta> list=new ArrayList<>(1);
            list.add(meta);
            return postDownload(context,fromAccount,folder,list);
        }
        return false;
    }

    public static boolean postDownload(Context context,String fromAccount,String folder, List<FileMeta> download) {
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
                    downloads.add(new Download(fromAccount,meta.getFile(),meta.getName(),folder,unique));
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
