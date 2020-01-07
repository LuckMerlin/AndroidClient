package com.merlin.player1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Looper;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.media.Indexer;
import com.merlin.media.Media;
import com.merlin.media.MediaPlayService;
import com.merlin.media.Mode;
import com.merlin.player.MediaBuffer;
import com.merlin.player.OnMediaFrameDecodeFinish;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Player;
import com.merlin.player.Status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPlayer extends Player implements OnPlayerStatusUpdate,OnMediaFrameDecodeFinish {
    private final List<Media> mQueue=new ArrayList<>();
    private AudioTrack mAudioTrack;
    private final Indexer mIndexer=new Indexer();
    private Mode mPlayMode;

    public MPlayer(){
        setOnDecodeFinishListener(this);
        mPlayMode=Mode.QUEUE_SORT;
    }

    @Override
    public void onMediaFrameDecodeFinish(int mediaType, byte[] bytes, int channels, int sampleRate) {
        final int length=null!=bytes?bytes.length:-1;
        if (length<=0){//Invalid frame data
            return;
        }
        AudioTrack audioTrack=buildAudioTrack(sampleRate,AudioFormat.CHANNEL_IN_STEREO);
        if (null==audioTrack){
            Debug.W(getClass(),"Can't play media frame. sampleRate="+sampleRate);
            return;
        }
        if (audioTrack.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){
            audioTrack.play();
        }
        audioTrack.write(bytes,0,length);
    }

    private AudioTrack buildAudioTrack(int sampleRateInHz,int channelConfig){
        if (sampleRateInHz<=0||channelConfig<=0){
            Debug.W(getClass(),"Can't build audio track.sampleRate="+sampleRateInHz+" channelConfig="+channelConfig);
            return null;
        }
        AudioTrack audioTrack=mAudioTrack;
        if (null!=audioTrack){
            int currSampleRateInHz=audioTrack.getSampleRate();
            int currChannelConfig=audioTrack.getChannelConfiguration();
            if (sampleRateInHz==currSampleRateInHz&&channelConfig==currChannelConfig){
                return audioTrack;
            }
            Debug.W(getClass(),"Release existed audio track when sampleRate changed."+
                    currSampleRateInHz+" "+sampleRateInHz+" "+currChannelConfig+" "+channelConfig);
            mAudioTrack=null;
            audioTrack.stop();
            audioTrack.release();
        }
        mAudioTrack=audioTrack=new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(sampleRateInHz,
                channelConfig, AudioFormat.ENCODING_PCM_16BIT),AudioTrack.MODE_STREAM);
        return audioTrack;
    }
    /////////////////
    public final boolean isExist(Object ...objs){
        return null!=index(objs);
    }

    public final int indexMediaPosition(Object ...objs){
        Object[] res=null!=objs&&objs.length>0?index(objs):null;
        Object object=null!=res&&res.length==2?res[0]:null;
        return null!=object&&object instanceof Integer ?(Integer) object:null;
    }

    public final Media indexMedia(Object ...objs){
        Object[] res=null!=objs&&objs.length>0?index(objs):null;
        Object object=null!=res&&res.length==2?res[1]:null;
        return null!=object&&object instanceof Media?(Media)object:null;
    }

    public final Object[] index(Object ...objs){
        if (null!=objs&&objs.length>0){
           for (Object obj:objs){
               if (null!=obj){
                   List<Media> queue=mQueue;
                   if (null!=queue){
                       synchronized (queue){
                         int size=null!=queue?queue.size():-1;
                          for (int i=0;i<size;i++){
                              Media m=queue.get(i);
                              if (null!=m){
                                   if (obj instanceof Media&&obj.equals(m)){
                                       return new Object[]{i,m};
                                   }else if (obj instanceof String){
                                        String path=m.getPath();
                                        if (null!=path&&path.equals(obj)){
                                            return new Object[]{i,m};
                                        }
                                   }
                              }
                          }
                       }
                   }
               }
           }
        }
        return null;
    }

    public final boolean append(Media media){
        return null!=media&&add(media,-1);
    }

    public final boolean add(Media media,int index){
        if (null!=media&&!isExist(media)){
            List<Media> queue=mQueue;
            if (null!=queue){
                int size=null!=queue?queue.size():0;
                synchronized (queue){
                     queue.add(index<0||index>size?size:index,media);
                     return true;
                }
            }
        }
        return false;
    }

    public final Mode playMode(Mode mode) {
        if (null!=mode){
            mPlayMode=mode;
            //Save mode here
            notifyPlayStatus(STATUS_MODE_CHANGED,"Play mode changed.",getPlaying(),mode);
        }
        return mPlayMode;
    }

    @Override
    protected final Pending onResolveNext() {
        Media media=indexQueueNext(false);
        return null!=media?new Pending(media,0):null;
    }

    public final boolean play(Object object, double seek, OnPlayerStatusUpdate update){
        if (null!=object){
            if (object instanceof Integer){
                int index=(Integer)object;
                int size;
                Media media;
                List<Media> playing=mQueue;
                if (null!=playing&&index>=0){
                    synchronized (playing){
                        size=playing.size();
                        media=index<size?playing.get(index):null;
                    }
                    if (null!=media){
                        return super.playMedia(media,seek,update);
                    }
                }
                return false;
            }else if (object instanceof Media){
                return super.playMedia((Media)object,seek,update);
            }
            Debug.W(getClass(),"Can't play media with seek."+object+" "+seek);
            return false;
        }else{//Play current paused media with seek
            if (seek>=0){
                return seek(seek);
            }
        }
        return false;
    }

    public final boolean playPre(){
        List<Media> queue=mQueue;
        Indexer indexer=mIndexer;
        Mode mode=mPlayMode;
        if (null!=queue&&null!=indexer){
            int nextIndex;
            synchronized (queue){
                int current=indexFromQueue(queue,getPlaying());
                int count=queue.size();
                nextIndex=indexer.pre(mode,current,count);
            }
            return play(nextIndex,0,null);
        }
        return false;
    }

    public final boolean playNext(boolean user){
        Media next=indexQueueNext(user);
        return null!=next&&play(next,0,null);
    }

    public final Media indexQueueNext(boolean user){
        List<Media> queue=mQueue;
        Indexer indexer=mIndexer;
        Mode mode=mPlayMode;
        if (null!=queue&&null!=indexer){
            int nextIndex;
            synchronized (queue){
                int current=indexFromQueue(queue,getPlaying());
                int count=queue.size();
                nextIndex=indexer.next(mode,current,count,user);
                return nextIndex>=0&&nextIndex<count?queue.get(nextIndex):null;
            }
        }
        return null;
    }

    public final boolean pause(boolean stop, Object... objs) {
        Object playing=getPlaying();
        objs=null!=playing&&null!=objs&&objs.length>0?objs:null;
        if (null!=objs&&objs.length>0){
            Object plyaingObj=playing instanceof Playable ?((Playable)playing).getPath():playing;
            String playingPath=null!=plyaingObj&&plyaingObj instanceof String?((String)plyaingObj):null;
            for (Object obj:objs){
                if (null==obj){
                    continue;
                }
                if (obj instanceof Playable&&obj.equals(playing)){
                    return pausePlay(stop);
                }else if (obj instanceof String&&null!=playingPath&&obj.equals(playingPath)){
                    return pausePlay(stop);
                }
            }
        }
        return false;
    }

    private void notifyPlayFinish(OnPlayerStatusUpdate update, Media media, boolean succeed, int status, String note){
        if (null!=update){
            update.onPlayerStatusUpdated(this,status,note,media,succeed);
        }
    }

    private int indexFromQueue(List<Media> queue,Object media){
        int size=null!=media&&null!=queue&&null!=media?queue.size():-1;
        if (size>0){
            Media child=null;
            for (int i=0;i<size;i++){
                child=queue.get(i);
                if (null!=child){
                    if(media instanceof Media&&child.equals(media)) {
                        return i;
                    }else if (media instanceof String&&child.equals(media)){
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public final List<Media> getQueue() {
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
    public void onPlayerStatusUpdated(Player player, int status, String note, Object media, Object data) {

    }
}
