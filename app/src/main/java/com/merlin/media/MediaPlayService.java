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

import com.merlin.api.Address;
import com.merlin.debug.Debug;
import com.merlin.global.Service;
import com.merlin.player.FileMedia;
import com.merlin.player.Playable;
import com.merlin.player1.MPlayer;
import com.merlin.player1.NasMedia;
import com.merlin.server.Retrofit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaPlayService extends Service {
    private final static String LABEL_MEDIAS = "medias";
    private final static String LABEL_POSITION = "position";
    private final static String LABEL_PLAY_TYPE = "playType";
    private final static List<ServiceConnection> mConnections = new ArrayList<>();
    private final Retrofit mRetrofit= new Retrofit(){
        @Override
        protected String onResolveUrl(Class<?> cls, Executor callbackExecutor) {
            return "http://192.168.0.3:5000";
        }
    };
    private final MPlayer mMPlayer=new MPlayer(null,null,mRetrofit );

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerBinder(){

            @Override
            public Playable getPlaying(Object arg,Boolean playing) {
                return mMPlayer.getPlaying(arg,playing);
            }

            @Override
            public boolean isPlaying(Object arg, Boolean playing) {
                return mMPlayer.isPlaying(arg,playing);
            }

            @Override
            public boolean toggle(int status, Object arg, String debug) {
                return mMPlayer.toggle(status,arg,debug);
            }

            @Override
            public ArrayList<Playable> getQueue(boolean containPlaying) {
                return mMPlayer.getQueue(containPlaying);
            }

            //            @Override
//            public boolean listener(int status, OnPlayerStatusChange change, String debug) {
//                return mMPlayer.listener(status,change,debug);
//            }
//
//            @Override
//            public boolean seek(double seek, Object arg, String debug) {
//                return mMPlayer.seek(seek,arg,debug);
//            }

            @Override
            public long getDuration(Object arg, String debug) {
                return mMPlayer.getDuration(arg,debug);
            }

            @Override
            public long getPosition(Object arg, String debug) {
                return mMPlayer.getPosition(arg,debug);
            }
        };
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
        String path3="./Test/摸摸.mp3";
        Playable media3=new NasMedia("http://192.168.0.3:5000","10134eb48ca85cad3da96deb57c3131b",null);
        mMPlayer.play(media3,0);
        mMPlayer.post(()->{
//            mMPlayer.seek(-
//                    1,"test");
        },0);
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
    public void onDestroy() {
        super.onDestroy();
        Debug.D(getClass(), "Player service onDestroy.");
        mMPlayer.release("While play service onDestroy.");
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
