package com.merlin.player1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.merlin.client.Client;
import com.merlin.debug.Debug;
import com.merlin.media.ClientMediaBuffer;
import com.merlin.media.Indexer;
import com.merlin.media.Media;
import com.merlin.media.Mode;
import com.merlin.player.MediaBuffer;
import com.merlin.player.FileBuffer;
import com.merlin.player.OnMediaFrameDecodeFinish;
import com.merlin.player.OnPlayerStatusUpdate;
import com.merlin.player.Playable;
import com.merlin.player.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MPlayer extends Player implements OnPlayerStatusUpdate,OnMediaFrameDecodeFinish {
    private final List<Media> mQueue=new ArrayList<>();
    private AudioTrack mAudioTrack;
    private final Indexer mIndexer=new Indexer();
    private Client mClient;
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
    public final boolean setClient(Client client){
        mClient=client;
        return false;
    }

    public final Client getClient() {
        return mClient;
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
            notifyPlayStatus(STATUS_MODE_CHANGED,"Play mode changed.",super.getPlaying(),mode);
        }
        return mPlayMode;
    }

    @Override
    protected final MediaBuffer onResolveNext(MediaBuffer buffer) {
        Playable playable=null!=buffer?buffer.getPlayable():null;
        Media media=indexQueueNext(playable,false);
        return null!=media?createMediaBuffer(media,0):null;
    }

    private MediaBuffer createMediaBuffer(Media media, double seek){
        if (null!=media){
            String path=media.getPath();//Try local media file firstly
            if (null!=path&&path.length()>0){
                File localFile=new File(path);
                if (localFile.exists()&&localFile.length()>0){
                    return new FileBuffer(media,seek);
                }
            }
            String url=media.getUrl();
            Client client=mClient;
            if (null!=url&&url.length()>0&&null!=client){
                return new ClientMediaBuffer(client,media,seek);
            }
        }
        return null;
    }

    public final Playable getPlayingMedia(Object ...objects){
        Playable playable=super.getPlaying();
        if (null!=playable){
            if (null!=objects&&objects.length>0){
                Media media;
                for (Object object:objects ) {
                    if (null!=object&&null!=(media=indexMedia(object))){
                        return media;
                    }
                }
                return null;
            }
            return playable;
        }
        return null;
    }

    public final boolean play(Media media,double seek,OnPlayerStatusUpdate update){
        MediaBuffer buffer=null!=media?createMediaBuffer(media,seek):null;
        if (null!=buffer){
            return play(buffer,update);
        }
        return false;
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
                        return play(media,seek,update);
                    }
                }
                return false;
            }else if (object instanceof Media){
                return play((Media)object,seek,update);
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
                int current=index(super.getPlaying());
                int count=queue.size();
                nextIndex=indexer.pre(mode,current,count);
            }
            return play(nextIndex,0,null);
        }
        return false;
    }

    public final boolean playNext(boolean user){
        Media next=indexQueueNext(super.getPlaying(),user);
        return null!=next&&play(next,0,null);
    }

    public final Media indexQueueNext(Playable media,boolean user){
        List<Media> queue=mQueue;
        Indexer indexer=mIndexer;
        Mode mode=mPlayMode;
        if (null!=queue&&null!=indexer){
            int nextIndex;
            int current=index(media);
            synchronized (queue){
                int count=queue.size();
                nextIndex=indexer.next(mode,current,count,user);
                return nextIndex>=0&&nextIndex<count?queue.get(nextIndex):null;
            }
        }
        return null;
    }

    public final boolean pause(boolean stop, Object... objects) {
        Playable playing=super.getPlaying();
        if (null!=playing){
            if (null!=objects&&objects.length>0){
                for (Object object:objects) {
                    if (null!=find(mQueue,object)){
                        return super.pause(stop);
                    }
                }
                return false;
            }
            return super.pause(stop);
        }
        return false;
    }

    private void notifyPlayFinish(OnPlayerStatusUpdate update, Media media, boolean succeed, int status, String note){
        if (null!=update){
            update.onPlayerStatusUpdated(this,status,note,media,succeed);
        }
    }

    @Override
    public void onPlayerStatusUpdated(Player player, int status, String note, Object media, Object data) {

    }

    public final boolean isExist(Object ...objs){
        return null!=indexInQueue(objs);
    }

    public final int index(Object media){
        Object[] objects= indexInQueue(media);
        Object object=null!=objects&&objects.length==2?objects[1]:null;
        return null!=object&&object instanceof Integer?(Integer) object:-1;
    }

    public final Media indexMedia(Object media){
        return null!=media?findMedia(mQueue,media):null;
    }

    private final Media findMedia(List<Media> queue,Object media){
        Object[] objects= find(queue,media);
        Object object=null!=objects&&objects.length==2?objects[0]:null;
        return null!=object&&object instanceof Media?(Media)object:null;
    }

    private Object[] indexInQueue(Object ...objects){
        List<Media> list=mQueue;
        if (null!=objects&&objects.length>0&&null!=list){
            Object[] result;
            for (Object object:objects) {
                if (null!=(result=null!=object?find(list,object):null)){
                    return result;
                }
            }
        }
        return null;
    }

    private Object[] find(List<Media> queue,Object media){
        if (null!=media&&null!=queue){
            synchronized (queue){
                int size=null!=media&&null!=queue&&null!=media?queue.size():-1;
                Media child;
                for (int i=0;i<size;i++){
                    child=queue.get(i);
                    if (null!=child){
                         if(media instanceof Media&&child.equals(media)) {
                            return new Object[]{media,i};
                        }else if (media instanceof String){
                            String path=child.getPath();
                            if (null!=path&&path.equals(media)) {
                                return new Object[]{media,i};
                            }
                        }
                    }
                }
            }
        }
        return null;
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

}
