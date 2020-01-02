package com.merlin.model;

import android.content.Context;
import android.database.DatabaseUtils;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaListAdapter;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.client.databinding.MediaSheetLayoutBinding;
import com.merlin.client.databinding.MediaSheetLayoutBindingImpl;
import com.merlin.database.DaoMaster;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.player.OnStateUpdate;
import com.merlin.player.Player;
import com.merlin.player1.MPlayer;
import com.merlin.protocol.What;
import com.merlin.util.FileMaker;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static com.merlin.player.Player.STATE_PROGRESS;

public class MediaPlayModel extends BaseModel implements BaseAdapter.OnItemClickListener, BaseModel.OnModelViewClick,OnStateUpdate {
    private ObservableField<Media> mPlaying=new ObservableField<>();
    private ObservableField<Integer> mPlayState=new ObservableField<>();
    private final MediaListAdapter mPlayingAdapter;
    private SeekBar mSeekBar;
    private final MPlayer mPlayer=new MPlayer();

    public MediaPlayModel(Context context){
        super(context);
        mPlayingAdapter=new MediaListAdapter(context);
        mPlayingAdapter.setOnItemClickListener(this);
        Player player=mPlayer;
        Media media=new Media();
        media.setPath("/sdcard/Musics/朴树 - 平凡之路.mp3");
        media.setTitle("平凡之路");
        media.setArtist("朴树");
//      medi
        List<Media> medias=getDatabaseSession(false).getMediaDao().queryBuilder().list();
        mPlayingAdapter.setData(medias);
//        getDatabaseSession(true).getMediaDao().insert(media);
//        long id, @NotNull String title, @NotNull String path, String md5,
//                String cloudUrl, String account, String album, String artist,
//        long duration
        mPlayingAdapter.add(new Media(0,"平凡之路","/sdcard/Musics/朴树 - 平凡之路.mp3",
                "md5","","linqiang","album","朴树",123131));
        mPlayingAdapter.add(new Media(0,"原点","/sdcard/Musics/西单女孩 - 原点.mp3",
                "md5","","linqiang","album","西单女孩",123131));
        mPlayingAdapter.add(new Media(0,"如果你还在就好了","/sdcard/Musics/如果你还在就好了.mp3",
                "md5","","linqiang","album","信",123131));
//        mPlayingAdapter.add(new Media("linqiang","我们都一样",""));
//        mPlayingAdapter.add(new Media("linqiang","我们不一样",""));
//        mPlayingAdapter.add(new Media("linqiang","原点",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
//        mPlayingAdapter.add(new Media("linqiang","平凡之路",""));
        mPlayer.setOnStateUpdateListener(this);
        mPlaying.set(mPlayingAdapter.getItem(0));//test
        mPlayer.play("/sdcard/Musics/朴树 - 平凡之路.mp3",0f);
        Debug.D(getClass(),"%%%%%%%% 牛 ");
//        ViewDataBinding binding=DataBindingUtil.getBinding(findViewById(R.id.test,View.class));
//        DataBindingUtil.bind(findViewById(R.id.test,View.class),new MediaSheetModel(getContext()));
//        Debug.D(getClass(),"444444444 "+binding);
//        DataBindingUtil.inflate(getLayoutInflater(),R.layout.media_sheet_layout,null,false);
//        DaoMaster.DevOpenHelper a = new DaoMaster.DevOpenHelper(this,"database_name",null);

//        File fileData=new File("/sdcard/Musics/西单女孩 - 原点.mp3");
//        final long fileLength=fileData.length();
//        byte[] bytes=new byte[1024*13];
//        int[] readed=new int[2];
//        FileInputStream fis= null;
//        try {
//            fis = new FileInputStream(fileData);
//            readed[0]=fis.read(bytes);
//            if (readed[0]>0) {
//                mPlayer.playBytes("/sdcard/a/林强.mp3", bytes, readed[0], fileLength);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        final FileInputStream fis=file;
//        new Thread(()->{
//            try {
//                readed[0]=fis.read(bytes);
//                if (readed[0]>0) {
//                    mPlayer.playByte("/sdcard/a/林强.mp3", bytes, readed[0], fileLength);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        test=true;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while (test||(readed[1]=fis.read(bytes))>0){
//                        if (readed[1]>0) {
//                            test=true;
//                            mPlayer.playByte("/sdcard/a/林强.mp3", bytes, readed[1], fileLength);
//                            readed[1]=0;
//                        }
//                    }
//                }catch (Exception e){
//
//                }
//            }
//        }).start();
//        mPlayer.play("/sdcard/Musics/朴树 - 平凡之路.mp3",0.0f);
//        mPlayer.playFile("/sdcard/Musics/朴树 - 平凡之路.mp3",0f);
//        Handler handler=new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//               mPlayer.getPlayerState();
//               mPlayer.getPosition();
//               mPlayer.pause(false);
//               Debug.D(getClass(),"################ 撒打发  "+mPlayer.getPlayerState());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPlayer.start(-1);
//                    }
//                },5000);https://files.pythonhosted.org/packages/5b/bb/cdc8086db1f15d0664dd22a62c69613cdc00f1dd430b5b19df1bea83f2a3/Pillow-6.2.1.tar.gz
////               mPlayer.seek(0.97f);
//            }
//        },13000);
//        new Thread(()->{
//            mPlayer.play("/sdcard/Musics/如果你还在就好了.mp3",0f);

//            Debug.D(getClass(),"的说法安抚 ");
//            mPlayer.play("/sdcard/Musics/西单女孩 - 原点.mp3",0);
//        }).start();
//        play(new Media("linqiang","操蛋","./WMDYY.mp3"),0);

        mPlayer.play("/sdcard/Musics/如果你还在就好了.mp3",0f);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mPlayer.pause();
            }
        },5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mPlayer.start(0.1f);
//            mPlayer.play("/sdcard/Musics/西单女孩 - 原点.mp3",0);
            }
        },100);
    }


    @Override
    public void onViewClick(View v, int id) {
        switch (id){
            case R.id.activityMediaPlay_preIV:
                break;
            case R.id.activityMediaPlay_playModeIV:

                break;
            case R.id.activityMediaPlay_playPauseIV:
                Player player=mPlayer;
                if (null!=player){
                    player.togglePausePlay();
                }
                break;
            case R.id.activityMediaPlay_nextIV:
                break;
        }
    }

    @Override
    public void onPlayerStateUpdated(int state, String path) {
//        post(()->{
//            switch (state){
//                case STATE_PROGRESS:
//                    updateProgress(mPlayer.getDuration(),mPlayer.getPosition());
//                    break;
//            }
//        });
    }

    private void updateProgress(long duration,long position){
        SeekBar seekBar=mSeekBar;
        if (null!=seekBar){
            seekBar.setProgress((int)(position*100.f/duration));
        }
    }

    @Override
    protected void onViewAttached(View root) {
        super.onViewAttached(root);
        MediaSheetLayoutBinding binding=DataBindingUtil.getBinding(findViewById(R.id.test,View.class));
        Debug.D(getClass(),"ddd "+binding);
        binding.setVm(new MediaSheetModel(getContext()));
//        DataBindingUtil.
//        DataBindingUtil.bind(findViewById(R.gid.test,View.class),new MediaSheetModel(getContext()));
        mSeekBar=findViewById(R.id.mediaPlayBottomProgressSB, SeekBar.class);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser){
                        mPlayer.seek(progress/100.f);
                    }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        updateProgress(mPlayer.getDuration(),mPlayer.getPosition());
//        getRoot().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toast("dddd "+mPlayer.getDuration()+" "+mPlayer.getPosition());
//                if (mPlayer.isPlaying()){
//                    mPlayer.pause();
//                }else{
//                    mPlayer.start(-1);
//                }
////            mPlayer.play("/sdcard/Musics/如果你还在就好了.mp3",0f);
//            }
//        });
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Object data) {
        if (null==data){
            return;
        }
        if (data instanceof Media){
            play((Media)data,0);
        }
    }

    private boolean play(Media media,long seek){
        final String path=null!=media?media.getPath():null;
        final MPlayer player=mPlayer;
        if (null==path||path.isEmpty()||null==player){
            Debug.W(getClass(),"Can't play media.Which url is NONE."+path+player);
            return false;
        }
        if (media.isLocalExist()){
            Debug.D(getClass(),"Play media for local file."+path);
            return  player.play(path,seek);
        }
        final String title=media.getTitle();
        if (null==title||title.isEmpty()){
            Debug.W(getClass(),"Can't play media.Which title is NONE."+title);
            return false;
        }
//        String cacheFolder=mCacheFolder;
//        final String cachePostfix=".cache";
//        cacheFolder=null!=cacheFolder&&cacheFolder.length()>0?cacheFolder:"/sdcard/a/cache";
//        final File cacheFile=new FileMaker().makeFile(cacheFolder,title+cachePostfix);
//        if (null==cacheFile||!cacheFile.exists()){
//            Debug.W(getClass(),"Can't play media which cache file create failed."+cacheFile+" "+url);
//            return false;
//        }
//        FileOutputStream os=null;
//        try {
//            os=new FileOutputStream(cacheFile,false);
//        } catch (FileNotFoundException e) {
//            Debug.E(getClass(),"Can't play media which cache file stream open fail.e="+e+" "+cacheFile,e);
//            closeIO(os);
//            return false;
//        }
//        final long currPosition=cacheFile.length();
//        final FileOutputStream fos=os;
//        final String account=media.getAccount();
//        seek=seek<currPosition?currPosition:seek; //If seek position less than current length
//        Debug.D(getClass(),"Play media on "+account+" "+seek+" "+url);
//        final Client.Canceler canceler=download(account, url,seek<=0?0:seek,(succeed, what, note, frame)->{
//            Debug.D(getClass(),"@@@ "+succeed+" "+note+" "+what+" "+frame);
//            if (succeed){
//                if (what==What.WHAT_HEAD_DATA){
//                    Debug.D(getClass(),"收到歌曲 信息 "+frame);
//                }else if(null!=frame){
//                    byte[] body =  frame.getBodyBytes() ;
//                    int length = null != body ? body.length : 0;
//                    if (length>0){
//                        try {
//                            fos.write(body,0,length);
//                            onMediaBytesReceived(body);
//                        } catch (IOException e) {
//                            Debug.E(getClass(),"Cache media file exception.e="+e+" "+cacheFile ,e);
//                        }
//                    }
//                }
//            }else{
//                closeIO(fos);
//                switch (what){
//                    case What.WHAT_NOT_ONLINE:
//                         toast(R.string.notOnline);
//                        break;
//                    case What.WHAT_NOT_EXIST:
//                        toast(R.string.fileNotExist);
//                        break;
//                    case What.WHAT_NONE_PERMISSION:
//                        toast(R.string.nonePermission);
//                        break;
//                }
//            }
//        });
//        return null!=canceler;
        return false;
    }

    public ObservableField<Integer> getPlayState() {
        return mPlayState;
    }

    public MediaListAdapter getPlayingAdapter() {
        return mPlayingAdapter;
    }

    public ObservableField<Media> getPlaying() {
        return mPlaying;
    }
}
