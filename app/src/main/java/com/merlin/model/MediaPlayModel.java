package com.merlin.model;

import android.content.Context;
import android.view.View;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaListAdapter;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.Media;
import com.merlin.player1.MPlayer;
import com.merlin.protocol.What;
import com.merlin.util.FileMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MediaPlayModel extends BaseModel implements BaseAdapter.OnItemClickListener {
    private final MediaListAdapter mPlayingAdapter;
    private final MPlayer mPlayer=new MPlayer();
    private String mCacheFolder;

    public MediaPlayModel(Context context){
        super(context);
        mPlayingAdapter=new MediaListAdapter(context);
        mPlayingAdapter.setOnItemClickListener(this);
        mPlayingAdapter.add(new Media("linqiang","3","./WMDYY.mp3"));
        mPlayingAdapter.add(new Media("linqiang","345",""));
        mPlayingAdapter.add(new Media("linqiang","55",""));
        mPlayingAdapter.add(new Media("linqiang","3453",""));
//        new Thread(()->{
//            try {
//                FileInputStream file=new FileInputStream("/sdcard/Musics/西单女孩 - 原点.mp3");
//                byte[] bytes=new byte[1024*1024];
//                int length=0;
//                while ((length=file.read(bytes))>0){
//                    Debug.D(getClass(),"##JAVA ### "+length);
//                    mPlayer.playBytes(bytes,0,length);
//                }
//                Debug.D(getClass(),"流播放结束了 ");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//        mPlayer.play("/sdcard/Musics/朴树 - 平凡之路.mp3",0.5f);
        new Thread(()->{
//            mPlayer.play("/sdcard/Musics/朴树 - 平凡之路.mp3",0f);
            mPlayer.play("/sdcard/Musics/如果你还在就好了.mp3",0f);
//            Debug.D(getClass(),"的说法安抚 ");
//            mPlayer.play("/sdcard/Musics/西单女孩 - 原点.mp3",0);
        }).start();
//        play(new Media("linqiang","操蛋","./WMDYY.mp3"),0);
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

    private void onMediaBytesReceived(byte[] bytes){

    }

    private boolean play(Media media,long seek){
        final String url=null!=media?media.getUrl():null;
        if (null==url||url.isEmpty()){
            Debug.W(getClass(),"Can't play media.Which url is NONE."+url);
            return false;
        }
        final String title=media.getTitle();
        if (null==title||title.isEmpty()){
            Debug.W(getClass(),"Can't play media.Which title is NONE."+title);
            return false;
        }
        String cacheFolder=mCacheFolder;
        final String cachePostfix=".cache";
        cacheFolder=null!=cacheFolder&&cacheFolder.length()>0?cacheFolder:"/sdcard/a/cache";
        final File cacheFile=new FileMaker().makeFile(cacheFolder,title+cachePostfix);
        if (null==cacheFile||!cacheFile.exists()){
            Debug.W(getClass(),"Can't play media which cache file create failed."+cacheFile+" "+url);
            return false;
        }
        FileOutputStream os=null;
        try {
            os=new FileOutputStream(cacheFile,false);
        } catch (FileNotFoundException e) {
            Debug.E(getClass(),"Can't play media which cache file stream open fail.e="+e+" "+cacheFile,e);
            closeIO(os);
            return false;
        }
        final long currPosition=cacheFile.length();
        final FileOutputStream fos=os;
        final String account=media.getAccount();
        seek=seek<currPosition?currPosition:seek; //If seek position less than current length
        Debug.D(getClass(),"Play media on "+account+" "+seek+" "+url);
        final Client.Canceler canceler=download(account, url,seek<=0?0:seek,(succeed, what, note, frame)->{
            Debug.D(getClass(),"@@@ "+succeed+" "+note+" "+what+" "+frame);
            if (succeed){
                if (what==What.WHAT_HEAD_DATA){
                    Debug.D(getClass(),"收到歌曲 信息 "+frame);
                }else if(null!=frame){
                    byte[] body =  frame.getBodyBytes() ;
                    int length = null != body ? body.length : 0;
                    if (length>0){
                        try {
                            fos.write(body,0,length);
                            onMediaBytesReceived(body);
                        } catch (IOException e) {
                            Debug.E(getClass(),"Cache media file exception.e="+e+" "+cacheFile ,e);
                        }
                    }
                }
            }else{
                closeIO(fos);
                switch (what){
                    case What.WHAT_NOT_ONLINE:
                         toast(R.string.notOnline);
                        break;
                    case What.WHAT_NOT_EXIST:
                        toast(R.string.fileNotExist);
                        break;
                    case What.WHAT_NONE_PERMISSION:
                        toast(R.string.nonePermission);
                        break;
                }
            }
        });
        return null!=canceler;
    }

    public MediaListAdapter getPlayingAdapter() {
        return mPlayingAdapter;
    }

}
