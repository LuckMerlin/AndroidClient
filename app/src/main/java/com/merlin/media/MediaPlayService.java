package com.merlin.media;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.bean.File;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.global.Service;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Status;
import com.merlin.player1.MPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MediaPlayService extends Service implements Status {
    private final MPlayer mPlayer = new MPlayer();
    private final static String LABEL_MEDIAS = "medias";
    private final static String LABEL_POSITION = "position";
    private final static String LABEL_PLAY_TYPE = "playType";
    private final static List<ServiceConnection> mConnections = new ArrayList<>();

    private final MediaPlayer mPlayerBinder = new MediaPlayer() {
        @Override
        public boolean play(Object object, float seek, OnPlayerStatusUpdate update) {
            MPlayer player = mPlayer;
            return null != player && player.play(object, seek, update,"After play call from binder.");
        }

        @Override
        public boolean pre(String debug) {
            MPlayer player = mPlayer;
            return null != player && player.playPre(debug);
        }

        @Override
        public boolean next(String debug) {
            MPlayer player = mPlayer;
            return null != player && player.playNext(true,debug);
        }

        @Override
        public boolean pause(boolean stop, Object... obj) {
            MPlayer player = mPlayer;
            return null != player && player.pause(stop, obj);
        }

        @Override
        public Mode playMode(Mode mode) {
            MPlayer player = mPlayer;
            return null != player ? player.playMode(mode) : null;
        }

        @Override
        public boolean seek(double seek, String debug) {
            MPlayer player = mPlayer;
            return null != player && player.seek(seek);
        }

        @Override
        public boolean togglePlayPause(Object media) {
            MPlayer player = mPlayer;
            return null != player && player.togglePausePlay(media);
        }

        @Override
        public int getPlayState() {
            MPlayer player = mPlayer;
            return null != player ? player.getPlayerStatus() : STATUS_UNKNOWN;
        }

        @Override
        public long getDuration() {
            MPlayer player = mPlayer;
            return null != player ? player.getDuration() : 0;
        }

        @Override
        public long getPosition() {
//            MPlayer player = mPlayer;
//            return null != player ? player.getPosition() : 0;
            return 0;
        }

        @Override
        public Playable getPlaying(Object... obj) {
            MPlayer player = mPlayer;
            return null != player ? player.getPlayingMedia(obj) : null;
        }

        @Override
        public List<Playable> getQueue() {
            MPlayer player = mPlayer;
            return null != player ? player.getQueue() : null;
        }

        @Override
        public boolean addListener(OnPlayerStatusUpdate update) {
            MPlayer player = mPlayer;
            return null != player && null != update && player.addListener(update);
        }

        @Override
        public boolean removeListener(OnPlayerStatusUpdate update) {
            MPlayer player = mPlayer;
            return null != player && null != update && player.removeListener(update);
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
        Debug.D(getClass(), "NasMedia play service onCreate.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = null != intent ? intent.getExtras() : null;
        handStartIntent(bundle);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handStartIntent(Bundle bundle) {
        if (null!=bundle){
            Object object=bundle.get(LABEL_MEDIAS);
            if (null!=object){
                if (object instanceof List){
                    final int size=((List)object).size();
                    if (size>0){
                        Object positionObj=bundle.get(LABEL_POSITION);
                        Object playTypeObj=bundle.get(LABEL_PLAY_TYPE);
                       final int playType=null!=playTypeObj&&playTypeObj instanceof Integer?((Integer)playTypeObj):MPlayer.PLAY_TYPE_NONE;
                       if ((playType&MPlayer.PLAY_TYPE_CLEAN_QUEUE)==MPlayer.PLAY_TYPE_CLEAN_QUEUE){
                            cleanPlayingQueue("After call from intent.");
                       }
                       final double seek=null!=positionObj&&positionObj instanceof Number?positionObj instanceof  Float||positionObj instanceof Double?((Double)positionObj):(Integer)positionObj:0;
                       if (null!=positionObj&&positionObj instanceof Integer){
                            if ((playType&MPlayer.PLAY_TYPE_ADD_INTO_QUEUE)==MPlayer.PLAY_TYPE_ADD_INTO_QUEUE){
                               addIntoQueue((List)object,"After call from intent.");
                            }
                        }
                        if ((playType&MPlayer.PLAY_TYPE_PLAY_NOW)==MPlayer.PLAY_TYPE_PLAY_NOW){
                            Object next=((List)object).get(0);
                            if (null!=next&&next instanceof Playable){
                                play((Playable)next,seek,"After call from intent.");
                            }
                        }
                        if ((playType&MPlayer.PLAY_TYPE_ORDER_NEXT)==MPlayer.PLAY_TYPE_ORDER_NEXT){
                            Object obj=((List)object).get(0);
                            if (null!=obj&&obj instanceof Playable){
                                setNext(((Playable)obj),seek,"After call from intent.");
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean cleanPlayingQueue(String debug){
        final MPlayer player=mPlayer;
        return null!=player&&player.cleanPlayingQueue(debug);
    }

    private boolean play(Playable playable,double seek,String debug){
        final MPlayer player=null!=playable?mPlayer:null;
        return null!=player&&player.play(playable,seek,null,debug);
    }

    private boolean addIntoQueue(List data,String debug){
        final MPlayer player=null!=data&&data.size()>0?mPlayer:null;
        if (null!=player){
            int count=0;
            for (Object obj:data){
                if (null!=obj&&obj instanceof NasMedia){
                    count=player.append((NasMedia)obj)?++count:count;
                }
            }
            toast(""+getText(R.string.add)+" "+String.format(""+getText(R.string.items),count));
            return true;
        }
        return false;
    }

    private boolean setNext(Playable playable,double seek,String debug){
        final MPlayer player=null!=playable?mPlayer:null;
        if (null!=player&&null!=player.setNext(playable,seek,debug)){
            return toast(getText(R.string.setNext)+ " "+playable.getTitle());
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(), "NasMedia play service onDestroy.");
        MPlayer player = mPlayer;
        if (null != player) {
            player.destroy();
        }
    }

    public static boolean start(Context context, Intent intent) {
        if (null != context) {
            intent = null != intent ? intent : new Intent();
            intent.setClass(context, MediaPlayService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startService(intent);
            } else {
                context.startService(intent);
            }
            return true;
        }
        Debug.W(MediaPlayService.class, "Can't start media play service. context=" + context);
        return false;
    }

    public static boolean play(Context context, Parcelable media, int position, int playType) {
        if (null != media && null != context) {
            ArrayList<Parcelable> list=new ArrayList<>(1);
            list.add(media);
            return play(context,list,position,playType);
        }
       return false;
    }

    public static boolean play(Context context, ArrayList<? extends Parcelable> medias, int position, int playType) {
        if (null != medias&&medias.size()>0 && null != context) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(LABEL_MEDIAS, medias);
            intent.putExtra(LABEL_POSITION, position);
            intent.putExtra(LABEL_PLAY_TYPE, playType);
            return start(context, intent);
        }
        return false;
    }

    /**
     * @deprecated
     */
    public static boolean play(Context context, File media, int position, boolean addIntoQueue){
        if (null!=media&&null!=context) {
            Intent intent = new Intent();
            intent.putExtra(LABEL_MEDIAS, media);
            intent.putExtra(LABEL_POSITION, position);
//            intent.putExtra(LABEL_ADD_INTO_QUEUE, addIntoQueue);
//            intent.putExtra(LABEL_PLAY, true);
            return start(context, intent);
        }
        Debug.W(MediaPlayService.class,"Can't play media by start service.media="+media+" context="+context);
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

}
