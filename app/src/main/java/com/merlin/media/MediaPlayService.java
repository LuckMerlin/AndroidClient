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

import com.merlin.bean.File_;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.global.Service;
import com.merlin.player.FileMedia;
import com.merlin.player.Playable;
import com.merlin.player1.MPlayer;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayService extends Service {
    private final MPlayer mPlayer = new MPlayer();
    private final static String LABEL_MEDIAS = "medias";
    private final static String LABEL_POSITION = "position";
    private final static String LABEL_PLAY_TYPE = "playType";
    private final static List<ServiceConnection> mConnections = new ArrayList<>();
    private final MPlayer mMPlayer=new MPlayer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MediaPlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Debug.D(getClass(), "Player service onCreate.");
        mMPlayer.run("While player service onCreate.");
        //test
        String path="/sdcard/Musics/大壮 - 我们不一样.mp3";
        String path2="/sdcard/Musics/赵雷 - 成都.mp3";
        Playable media=new FileMedia(path);
        Playable media2=new FileMedia(path2);
        String path3="./摸摸.mp3";
//        Playable media3=new NasMedia(null,path3,"http://192.168.0.3:5000");
        mMPlayer.play(media2,0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = null != intent ? intent.getExtras() : null;
        handStartIntent(bundle);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handStartIntent(Bundle bundle) {
        if (null!=bundle) {
//            Object object=bundle.get(LABEL_MEDIAS);
//            if (null!=object){
//                if (object instanceof List){
//                    final int size=((List)object).size();
//                    if (size>0){
//                        Object positionObj=bundle.get(LABEL_POSITION);
//                        Object playTypeObj=bundle.get(LABEL_PLAY_TYPE);
//                       final int playType=null!=playTypeObj&&playTypeObj instanceof Integer?((Integer)playTypeObj):MPlayer.PLAY_TYPE_NONE;
//                       if ((playType&MPlayer.PLAY_TYPE_CLEAN_QUEUE)==MPlayer.PLAY_TYPE_CLEAN_QUEUE){
//                            cleanPlayingQueue("After call from intent.");
//                       }
//                       final double seek=null!=positionObj&&positionObj instanceof Number?positionObj instanceof  Float||positionObj instanceof Double?((Double)positionObj):(Integer)positionObj:0;
//                       if (null!=positionObj&&positionObj instanceof Integer){
//                            if ((playType&MPlayer.PLAY_TYPE_ADD_INTO_QUEUE)==MPlayer.PLAY_TYPE_ADD_INTO_QUEUE){
//                               addIntoQueue((List)object,"After call from intent.");
//                            }
//                        }
//                        if ((playType&MPlayer.PLAY_TYPE_PLAY_NOW)==MPlayer.PLAY_TYPE_PLAY_NOW){
//                            Object next=((List)object).get(0);
//                            if (null!=next&&next instanceof IPlayable){
//                                play((IPlayable)next,seek,"After call from intent.");
//                            }
//                        }
//                        if ((playType&MPlayer.PLAY_TYPE_ORDER_NEXT)==MPlayer.PLAY_TYPE_ORDER_NEXT){
//                            Object obj=((List)object).get(0);
//                            if (null!=obj&&obj instanceof IPlayable){
//                                setNext(((IPlayable)obj),seek,"After call from intent.");
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    @Override
    public void onDestroy()

    {
        super.onDestroy();
        Debug.D(getClass(), "Player service onDestroy.");
        mPlayer.release("While play service onDestroy.");
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

    public static boolean play(Context context, Parcelable media, double seek) {
        if (null != media && null != context) {
            ArrayList<Parcelable> list=new ArrayList<>(1);
            list.add(media);
            return play(context,list,seek);
        }
       return false;
    }

    public static boolean play(Context context, ArrayList<? extends Parcelable> medias, double seek) {
        if (null != medias&&medias.size()>0 && null != context) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(LABEL_MEDIAS, medias);
            intent.putExtra(LABEL_POSITION, seek);
            return start(context, intent);
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

}
