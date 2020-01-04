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
import com.merlin.player1.MPlayer;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayService extends Service {
    private final List<Media> mPlaying=new ArrayList<>();
    private final MPlayer mPlayer=new MPlayer();
    private final static String LABEL_MEDIAS="medias";
    private final static String LABEL_POSITION="position";
    private final static String LABEL_INDEX="index";
    private final static List<ServiceConnection> mConnections=new ArrayList<>();
    private Client mClient;
    private final MediaPlayer mPlayerBinder=new MediaPlayer(){
        @Override
        public boolean play(int index, int position, Object... obj) {
            return false;
        }

        @Override
        public boolean pause(boolean stop, Object... obj) {
            return false;
        }

        @Override
        public boolean start(int position, Object... obj) {
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
            List<Media> list=mPlaying;
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
        media.setName("我不愿让你一个人");
        media.setAccount("linqiang");
        media.setUrl("./WMDYY.mp3");
        mPlaying.add(media);
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
                    Intent intent=new Intent();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(),"Media play service onDestroy.");
    }
}
