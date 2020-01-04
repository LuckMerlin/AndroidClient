package com.merlin.media;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.global.Application;
import com.merlin.player.OnStateUpdate;
import com.merlin.player.Player;
import com.merlin.player1.MPlayer;
import com.merlin.server.Frame;
import com.merlin.task.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayService extends Service {
    private final List<Media> mQueue=new ArrayList<>();
    private final MPlayer mPlayer=new MPlayer();
    private final static String LABEL_MEDIAS="medias";
    private final static String LABEL_POSITION="position";
    private final static String LABEL_INDEX="index";
    private Object mPlaying;
    private final static List<ServiceConnection> mConnections=new ArrayList<>();
    private Client mClient;
    private final MediaPlayer mPlayerBinder=new MediaPlayer(){
        @Override
        public boolean play(Object object, float seek,OnStateUpdate update) {
            if (null!=object){
                if (object instanceof Integer){
                    int index=(Integer)object;
                    int size=0;
                    Media media;
                    List<Media> playing=mQueue;
                    if (null!=playing&&index>=0){
                        synchronized (playing){
                            size=null!=playing?playing.size():-1;
                            media=index>=0&&index<size?playing.get(index):null;
                        }
                        if (null!=media){
                            return doPlay(media,seek,update);
                        }
                    }
                    Debug.W(getClass(),"Can't play media,Index none.index="+index+" size="+size);
                    return false;
                }else if (object instanceof Media){
                    return doPlay((Media)object,seek,update);
                }
                Debug.W(getClass(),"Can't play media with seek."+object+" "+seek);
                return false;
            }else{//Play current paused media with seek
                Object playing=mPlaying;
                MPlayer player=mPlayer;
                if (seek>=0){
                    return player.seek(seek)>=0;
                }
            }
            return true;
        }

        @Override
        public boolean pause(boolean stop, Object... obj) {
            return false;
        }

        @Override
        public int getPlayState() {
            return 0;
        }

        @Override
        public Media getPlaying(Object... obj) {
            return null;
        }

        @Override
        public List<Media> getQueue() {
            List<Media> list=mQueue;
            int size=null!=list?list.size():0;
            if (size>0){
                List<Media> result=new ArrayList<>(size);
                result.addAll(list);
                return result;
            }
            return null;
        }

        @Override
        public boolean putListener(OnStateUpdate update) {
            mPlayer.setOnStateUpdateListener(update);
            return true;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mPlayerBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.D(getClass(),"Media play service onCreate.");
        Application application= Application.get(this);
        mClient=null!=application?application.getClient():null;
        Media media=new Media();
        media.setName("我不愿让你一个人.mp3");
        media.setAccount("linqiang");
        media.setUrl("./WMDYY.mp3");
        mQueue.add(media);
    }

    private boolean doPlay(Media media,float seek,OnStateUpdate update){
            return doPlay(media,null,seek,update);
    }

    private boolean doPlay(Media media,String filePath,float seek,OnStateUpdate update){
        MPlayer player=null!=media?mPlayer:null;
        if (null!=media){
            final String localPath=null!=filePath&&filePath.length()>0?filePath:media.getPath();
            final File localFile=null!=localPath&&localPath.length()>0?new File(localPath):null;
            if (null!=localFile&&localFile.length()>0){//If play local file
                Debug.D(getClass(),"Play media with local file."+localPath);
                return player.play(localPath,seek);
            }else {
                Client client=mClient;
                String url=media.getUrl();
                String account= media.getAccount();
                String name=media.getName();
                if (null!=url&&url.length()>0&&null!=name&&name.length()>0){
                     if (!client.isLogined()){
                         notifyPlayFinish(update,media,false,Status.FINISH_START_FAIL,"Client not login.");
                        return false;
                     }
                     Debug.D(getClass(),"Caching media file from url "+account+" "+url);
                     if (null!=update){
                         update.onPlayerStateUpdated(Status.CACHING,url);
                     }
                    final Client.Canceler canceler=client.downloadFile(account,url,"/sdcard/a/temp.mp3",
                            (finish,what, accountValue,urlValue,to,data)->{
                            Debug.D(getClass(),"#### "+finish+" "+what);
                            if (finish&&what== Client.OnFileDownloadUpdate.DOWNLOAD_SUCCEED){
                                File download=null!=to&&to.length()>0?new File(to):null;
                                if (null!=download&&download.length()>0){
                                    String playPath=to;
                                    if (null!=localFile){
                                        download.renameTo(localFile);
                                        playPath=localFile.getAbsolutePath();
                                    }
                                    Object playingObj=mPlaying;//Check if current
                                    Media playing=null!=playingObj&&playingObj instanceof CachingMedia?((CachingMedia)playingObj).mMedia:null;
                                    if (null==playing||playing!=media){//If need play

                                    }else{
                                        doPlay(media,playPath,seek,update);
                                    }
                                }
                            }
                    });
                     if (null!=canceler){
                         mPlaying=new CachingMedia(media,canceler);
                         return true;
                     }
                    Debug.D(getClass(),"Fail cache media file from url "+account+" "+url);
                    notifyPlayFinish(update,media,false,Status.FINISH_START_FAIL,"Cache failed.");
                    return false;
                }
                notifyPlayFinish(update,media,false,Status.FINISH_START_FAIL,"Args invalid.name="+name +" url="+url);
                return false;
            }
        }
        notifyPlayFinish(update,media,false,Status.FINISH_START_FAIL,"Media invalid.");
        return false;
    }

    public static boolean start(Context context, Intent intent){
        if (null!=context){
            intent=null!=intent?intent:new Intent();
            intent.setClass(context,MediaPlayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            }else{
                context.startService(intent);
            }
            return true;
        }
        return false;
    }

    public static boolean add(Context context,Media media,int index){
        if (null!=context&&null!=media){
            Intent intent=new Intent();
            intent.putExtra(LABEL_MEDIAS,media);
            intent.putExtra(LABEL_INDEX,index);
            return start(context,intent);
        }
        return false;
    }

    public static boolean bind(Activity activity){
        if (null!=activity){
            if (mConnections.contains(activity)){
                Debug.W(MediaPlayService.class,"Already bind activity.");
                return false;
            }
            if (start(activity,null)){
                if (activity instanceof ServiceConnection){
                    Intent intent=new Intent(activity,MediaPlayService.class);
                    Debug.D(MediaPlayService.class,"Bind media play service for "+activity.getClass());
                    mConnections.add((ServiceConnection)activity);
                    if(activity.bindService(intent,(ServiceConnection)activity,Context.BIND_AUTO_CREATE)){
                        return true;
                    }
                    mConnections.remove(activity);
                    Debug.W(MediaPlayService.class,"Bind media play service failed.");
                    return false;
                }
                Debug.W(MediaPlayService.class,"Can't bind media play service.Activity not extends service connection.");
                return false;
            }
            Debug.W(MediaPlayService.class,"Can't bind media play service,Start failed.");
            return false;
        }
        return false;
    }

    public static boolean unbind(Activity activity){
        if (null!=activity){
            if (activity instanceof ServiceConnection){
                Debug.D(MediaPlayService.class,"UnBind media play service for "+activity.getClass());
                mConnections.remove(activity);
                activity.unbindService((ServiceConnection)activity);
                return true;
            }
            return false;
        }
        Debug.W(MediaPlayService.class,"Can't bind media play service.Activity not extends service connection.");
        return false;
    }

    public static boolean isRunning(Context context){
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
//        if (!(serviceList.size() > 0)) {
//            return false;
//        }
//        for (int i = 0; i < serviceList.size(); i++) {
//            RunningServiceInfo serviceInfo = serviceList.get(i);
//            ComponentName serviceName = serviceInfo.service;
//
//            if (serviceName.getClassName().equals(className)) {
//                return true;
//            }
//        }
        return false;
    }

    private void notifyPlayFinish(OnStateUpdate update,Media media,boolean succeed,int status,String note){
        if (null!=update){
            update.onPlayerStateUpdated(status,null!=media?media.getPath():null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"Media play service onDestroy.");
    }


    private static class PendingMedia{
        private final Media mMedia;
        private PendingMedia(Media media){
            mMedia=media;
        }
    }

    private static class CachingMedia{
        private final Media mMedia;
        private final Client.Canceler mCanceler;
        public CachingMedia(Media media, Client.Canceler canceler){
            mMedia=media;
            mCanceler=canceler;
        }
    }
}
