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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.merlin.bean.FileMeta;
import com.merlin.breakpoint.BreakPoint;
import com.merlin.breakpoint.BreakPointer;
import com.merlin.breakpoint.ShBreakPointer;
import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.protocol.Tag;
import com.merlin.protocol.What;
import com.merlin.server.Response;

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
    private final List<Download> mRunningList=new ArrayList<>();
    private final Handler mHandler=new Handler(Looper.getMainLooper());
    private final Map<Download, Client.Canceler> mDownloading=new HashMap<>();
    private WeakReference<Callback> mCallback;
    private BreakPointer mBreakPointer;
    private int mMaxDownloading=1;
    private Client mClient;
    private final Binder mBinder=new Binder();

    public interface Callback extends Status{
        void onFileDownloadUpdate(int what,boolean finish,Download task,Object data);
    }

    public class Binder extends android.os.Binder implements Downloader{
       public  List<Download>  getDownloadList(){
               synchronized (mRunningList) {
                   List<Download> list = new ArrayList<>(mRunningList.size());
                   list.addAll(mRunningList);
                   return list;
               }
        }

        public int getDownloadingSize(){
            Map<Download, Client.Canceler> downloading=mDownloading;
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
                for (Download child:mRunningList){
                    if (null!=child&&child.equals(download)){
                        return true;
                    }
                }
            }
           return false;
        }

        public boolean isDownloading(Download download){
            synchronized (mRunningList) {
                for (Download child:mRunningList){
                    if (null!=child&&child.equals(download)){
                        return child.getStatus()==Status.DOWNLOADING;
                    }
                }
            }
           return false;
        }

        public boolean pause(Download task){
            Map<Download, Client.Canceler> downloading=mDownloading;
            if (null!=task&&null!=downloading){
                synchronized (downloading){
                    Client.Canceler canceler=downloading.get(task);
                    return null!=canceler&&canceler.cancel(true);
                }
            }
            return false;
        }

        public boolean cancel(Download download){
            if (null!=download){
                if (null!=download){
                    download.setDeleteIncomplete(true);
                }
                return pause(download);
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
        BreakPointer pointer=mBreakPointer=new ShBreakPointer(app);
        List<BreakPoint> list=pointer.getBreakpoints();//Init break list
        if (null!=list&&list.size()>0){
            for (BreakPoint point:list){
                Download download=null!=point?point.getTask():null;
                if (null!=download){ //Add task into queue
                    download.setStatus(Status.PAUSE); //Reset status to pause
                    download.buildRemainFromFile();
                    mRunningList.add(download);
                    Debug.D(getClass(),"Add breakpoint file."+download);
                    onFileDownloadUpdate(Status.ADD,false,download,null);
                }
            }
            checkDownloadNextPossible();//Check
        }
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

    public void onFileDownloadUpdate(int what,boolean finish,Download download,Object data){
        WeakReference<Callback> reference=mCallback;
        Callback callback=null!=reference?reference.get():null;
        Handler handler=mHandler;
        if (null!=callback&&null!=handler){
            handler.post(()->callback.onFileDownloadUpdate(what,finish,download,data));
        }
    }

    private Client.Canceler download(final Download download){
        String fromAccount=null!=download?download.getFromAccount():null;
        String name=null!=download?download.getTargetName():null;
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
        final List<Download> runningList=mRunningList;
        final Map<Download, Client.Canceler> downloading=mDownloading;
        if (null==runningList){
            Debug.W(getClass(),"Can't download file.runningList="+runningList+" "+download);
            return null;
        }
        final Binder binder=mBinder;
        int index=runningList.indexOf(download);
        Download existTask;
        synchronized (runningList) {
            existTask = index >= 0 ? runningList.get(index) : null;
        }
        int status=null!=existTask?existTask.getStatus():Status.INVALID;
        if (status==Status.DOWNLOADING){
            Debug.W(getClass(),"Can't download file.Exist downloading."+download);
            return null;
        }
        download.setStatus(Status.UNKNOWN);
        int downloadingSize=binder.getDownloadingSize();
        if (downloadingSize>0){//Check if need waiting
            int maxDownloading=mMaxDownloading;
            if (downloadingSize>(maxDownloading<=0?1:maxDownloading)){
                Debug.W(getClass(),"Add download into wait queue."+downloadingSize+" "+maxDownloading);
                download.setStatus(Status.WAITING);
                onFileDownloadUpdate(Callback.WAITING,true,download,System.currentTimeMillis());
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
                    download.setStatus(Status.FINISH_WRITE_EXCEPTION);
                    return null;
                }
            }
        }
        final long seek=download.getType()==Download.TYPE_REPLACE?0:targetFile.length();
        Debug.D(getClass(),"Downloading file."+download.getType()+" "+fromAccount+" "+seek+" \n from:"+srcPath+"\n to:"+targetFile);
        FileOutputStream os=null;
        Client.Canceler canceler=null;
        try {
            final FileOutputStream fos=os=new FileOutputStream(targetFile,seek<=0?false:true);
            long startTime=System.currentTimeMillis();
            runningList.remove(download);
            runningList.add(download);
            onFileDownloadUpdate(Callback.START,false,download,startTime);
            download.setStatus(Status.DOWNLOADING);
            final BreakPoint breakPoint=new BreakPoint(download);
            mBreakPointer.addBreakpoint(breakPoint);
            canceler=client.download(fromAccount, srcPath,seek<=0?0:seek,(succeed,what,note,frame)->{
                if (succeed) {
                    if (null==frame){
                        return;
                    }
                    long remain=frame.getRemain();
                    download.setRemain(remain);
                    if (what==What.WHAT_HEAD_DATA){//If response data,Not bytes data
                        String bodyText=null!=frame?frame.getBodyText():null;
                        JSONObject json=null!=bodyText&&bodyText.length()>0? JSON.parseObject(bodyText):null;
                        if (null!=json){
                            download.setSrcPath(json.getString(Tag.TAG_FILE));
                            download.setTotal(json.getLongValue(Tag.TAG_SIZE));
                            download.setMD5(json.getString(Tag.TAG_MD5));
                            download.setFormat(json.getString(Tag.TAG_FORMAT));
                        }
                        onFileDownloadUpdate(Callback.DOWNLOADING, false, download, remain);
                        return;
                    }
                    byte[] body =  frame.getBodyBytes() ;
                    int length = null != body ? body.length : 0;
                    if (length>0){
                        try {
                            fos.write(body,0,length);
                            if (frame.isLastFrame()){
                                removeAllTask(download);
                                closeStream(fos);
                                download.setStatus(Status.FINISH_SUCCEED);
                                mBreakPointer.removeBreakpoint(download);
                                onFileDownloadUpdate(Callback.FINISH_SUCCEED,true,download,System.currentTimeMillis());
                                Debug.D(getClass(),"下载完成了 "+note);
                                checkDownloadNextPossible();
                            }else{
                                onFileDownloadUpdate(Callback.DOWNLOADING, false, download, remain);
                            }
                        }catch (Exception e){
                            Debug.E(getClass(),"Failed download file.e="+e+" \n"+targetFile,e);
                            closeStream(fos);
                            targetFile.delete();
                            removeAllTask(download);
                            if (download.isDeleteIncomplete()){
                                mBreakPointer.removeBreakpoint(breakPoint);
                                targetFile.delete();
                            }
                            download.setStatus(Status.FINISH_WRITE_EXCEPTION);
                            onFileDownloadUpdate(Callback.FINISH_WRITE_EXCEPTION,true,download,e);
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
                    if (canceled){
                        download.setDeleteIncomplete(false);
                        download.setType(Download.TYPE_NORMAL);
                        download.setStatus(Status.PAUSE);
                        removeDownloadingTask(download);
                    }else {
                        removeAllTask(download);
                    }
                    if (alreadyDownloaded){
                        mBreakPointer.removeBreakpoint(download);
                        download.setStatus(Status.FINISH_SUCCEED);
                        onFileDownloadUpdate(Callback.FINISH_SUCCEED,true,download,null);
                    }else{
                        if (download.isDeleteIncomplete()){
                            targetFile.delete();
                            mBreakPointer.removeBreakpoint(download);
                        }
                        if (canceled) {
                            download.setStatus(Status.FINISH_CANCEL);
                            onFileDownloadUpdate(Callback.FINISH_CANCEL, true, download, null);
                        }else {
                            download.setStatus(Status.FINISH_SERVICE_FAIL);
                            onFileDownloadUpdate(Callback.FINISH_SERVICE_FAIL, true, download, null);
                        }
                    }
                }
            });
            if (null!=canceler){
                downloading.put(download,canceler);
            }else{
                download.setStatus(Status.FINISH_START_FAIL);
            }
            return canceler;
        } catch (Exception e) {
            Debug.E(getClass(),"Failed download file.e="+e,e);
            targetFile.delete();
        }finally {
            if (null==canceler&&null!=os){
                Debug.E(getClass(),"Close file OutputStream While download fail. "+targetFile);
                closeStream(os);
                download.setStatus(Status.FINISH_START_FAIL);
                removeAllTask(download);
                if (download.isDeleteIncomplete()){
                    targetFile.delete();
                    mBreakPointer.removeBreakpoint(download);
                }
                onFileDownloadUpdate(Callback.FINISH_START_FAIL,true,download,null);
            }
        }
        return null;
    }

    private boolean removeAllTask(Download task){
        removeRunningTask(task);
        removeDownloadingTask(task);
        return true;
    }

    private boolean removeRunningTask(Download task){
        List<Download> running=mRunningList;
        if (null!=running){
            removeTask(running,task);
        }
        return true;
    }

    private boolean removeDownloadingTask(Download task){
        Map<Download, Client.Canceler> downloading=mDownloading;
        if (null!=downloading){
            Set<Download> set;
            synchronized (downloading){
                set=downloading.keySet();
            }
            if (null!=set){
                removeTask(set,task);
            }
        }
        return true;
    }


    private boolean removeTask(Collection<Download> collection,Download task){
        if (null!=collection&&null!=task){
            synchronized (collection){
            for (Download dt:collection){
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
          List<Download> running=mRunningList;
          if (null!=running){
              Download next = null;
              synchronized (running) {
                  for (Download task : running) {
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
                      download(next);
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
