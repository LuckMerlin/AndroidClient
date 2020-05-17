package com.merlin.player1;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.merlin.bean.NasMedia;
import com.merlin.debug.Debug;
import com.merlin.media.Indexer;
import com.merlin.media.Mode;
import com.merlin.media.NasMediaBuffer;
import com.merlin.media.NetMediaBuffer;
import com.merlin.player.BK_Player;
import com.merlin.player.IPlayable;
import com.merlin.player.MediaBuffer;
import com.merlin.player.OnMediaFrameDecodeFinish;
import com.merlin.player.OnPlayerStatusUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MPlayer extends BK_Player implements OnMediaFrameDecodeFinish,OnPlayerStatusUpdate {
    public static final int PLAY_TYPE_NONE = 0x00; //0000 0000
    public static final int PLAY_TYPE_ORDER_NEXT = 0x01; //0000 0001
    public static final int PLAY_TYPE_PLAY_NOW = 0x02; //0000 0010
    public static final int PLAY_TYPE_ADD_INTO_QUEUE = 0x04; //0000 0100
    public static final int PLAY_TYPE_CLEAN_QUEUE = 0x08; //0000 1000
    private final List<IPlayable> mQueue=new ArrayList<>();
    private AudioTrack mAudioTrack;
    private final Indexer mIndexer=new Indexer();
    private Mode mPlayMode;

    public MPlayer(){
        addListener(this);
        mPlayMode=Mode.RANDOM;
    }

    @Override
    public void onMediaFrameDecodeFinish(int mediaType, byte[] bytes, int channels, int sampleRate, int speed,long currPosition) {
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

    public final Mode playMode(Mode mode) {
        if (null!=mode){
            Mode curr=mPlayMode;
            curr = null!=curr?curr:Mode.QUEUE_SORT;
            if (mode==Mode.CHANGE_MODE){
                Mode[] modes=Mode.values();
                int length=null!=modes?modes.length:-1;
                if (length>1){
                    int index=(Arrays.binarySearch(modes,curr)+1);
                    index=index>=0&&index<length?index:1;
                    mPlayMode=modes[index];
                }
            }else{
                mPlayMode=mode;
            }
            //Save mode here
            notifyPlayStatus(STATUS_MODE_CHANGED,"Play mode changed.",super.getPlaying());
        }
        return mPlayMode;
    }

    @Override
    public void onPlayerStatusUpdated(BK_Player player, int status, String note, IPlayable media, Object data) {
    }

    @Override
    protected final MediaBuffer onResolveNext(MediaBuffer buffer) {
        IPlayable playable=null!=buffer?buffer.getPlayable():null;
        Mode mode=mPlayMode;
        IPlayable played=null!=mode&&mode==Mode.SINGLE?buffer.getPlayable():null;
        if (null!=mode&&mode==Mode.SINGLE&&null!=played){
            return createMediaBuffer(played,0);
        }
        IPlayable media=indexQueueNext(playable,false);
        return null!=media?createMediaBuffer(media,0):null;
    }

    private MediaBuffer createMediaBuffer(IPlayable media, double seek){
        if (null!=media){
            if (media instanceof NasMedia){
                return new NasMediaBuffer((NasMedia)media,seek);
            }
//            String path=media.getPath();//Try local media file firstly
//            if (null!=path&&path.length()>0){
//                File_ localFile=new File_(path);
//                if (localFile.exists()&&localFile.length()>0){
//                    return new FileBuffer(media,seek);
//                }
//            }
//            String url=media.getPath();
//            if (null!=url&&url.length()>0){
//                if (media instanceof File_){
//                    return new __NetMediaBuffer((File_) media,seek);
//                }
//            }
        }
        return null;
    }

    public final MediaBuffer setNext(IPlayable playable, double seek, String debug){
        MediaBuffer buffer=null!=playable?createMediaBuffer(playable,seek):null;
        return null!=buffer&&setNext(buffer,debug)?buffer:null;
    }

    public final boolean cleanPlayingQueue(String debug){
        List<IPlayable> queue=mQueue;
        int size=null!=queue?queue.size():-1;
        if (size>0){
            Debug.D(getClass(),"Clean playing queue("+size+")"+(null!=debug?debug:"."));
            queue.clear();
            return true;
        }
        return false;
    }

    public final boolean play(IPlayable playable, double seek, OnPlayerStatusUpdate update, String debug){
        if (null!=playable){
             MediaBuffer buffer=createMediaBuffer(playable,seek);
             if (null!=buffer){
                return play(buffer,update,debug);
             }
             Debug.W(getClass(),"Can't play media which create media buffer fail "+(null!=debug?debug:"."));
            return false;
        }
        Debug.W(getClass(),"Can't play media with is NULL."+(null!=debug?debug:"."));
        return false;
    }

    public final boolean play(Object object, double seek, OnPlayerStatusUpdate update,String debug){
        if (null!=object){
            if (object instanceof Integer){
                int index=(Integer)object;
                int size;
                IPlayable media;
                List<IPlayable> playing=mQueue;
                if (null!=playing&&index>=0){
                    synchronized (playing){
                        size=playing.size();
                        media=index<size?playing.get(index):null;
                    }
                    if (null!=media) {
                        return play(media, seek, update,debug);
                    }
                }
                Debug.W(getClass(),"Can't play media.index="+index+" playing="+playing);
                return false;
            }else if (object instanceof IPlayable){
                return play((IPlayable)object,seek,update,debug);
            }
            Debug.W(getClass(),"Can't play media with seek."+object+" "+seek);
            return false;
        }else{//Play current paused media with seek
            if (seek>=0){
                return seek(seek);
            }
        }
        Debug.W(getClass(),"Can't play media.object="+object+" seek="+seek);
        return false;
    }

    public synchronized final boolean append(IPlayable media){
        return null!=media&&add(media,-1);
    }

    public synchronized final boolean add(IPlayable media, int index){
        Boolean exist=null!=media?!isExist(media):null;
        if (null!=exist&&exist){
            List<IPlayable> queue=mQueue;
            if (null!=queue){
                int size=null!=queue?queue.size():0;
                synchronized (queue){
                    queue.add(index<0||index>size?size:index,media);
                    return true;
                }
            }
            return false;
        }
        Debug.W(getClass(),"Can't add media into queue.exist="+exist);
        return false;
    }

    public final int getQueueSize(){
        List<IPlayable> queue=mQueue;
        return null!=queue?queue.size():-1;
    }

    public final IPlayable getPlayingMedia(Object ...objects){
        IPlayable playable=super.getPlaying();
        if (null!=playable){
            if (null!=objects&&objects.length>0){
                IPlayable media;
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

    public final boolean playPre(String debug){
        List<IPlayable> queue=mQueue;
        Indexer indexer=mIndexer;
        Mode mode=mPlayMode;
        if (null!=queue&&null!=indexer){
            int nextIndex;
            synchronized (queue){
                int current=index(super.getPlaying());
                int count=queue.size();
                IPlayable playing=count<=0?getPlaying():null;
                if (null!=playing){
                    return play(playing,0,null,debug);
                }
                nextIndex=indexer.pre(mode,current,count);
            }
            return play(nextIndex,0,null,debug);
        }
        return false;
    }

    public final boolean playNext(boolean user,String debug){
        IPlayable next=indexQueueNext(super.getPlaying(),user);
        return null!=next&&play(next,0,null,debug);
    }

    public final IPlayable indexQueueNext(IPlayable media, boolean user){
        List<IPlayable> queue=mQueue;
        Indexer indexer=mIndexer;
        Mode mode=mPlayMode;
        if (null!=queue&&null!=indexer){
            int nextIndex;
            int current=index(media);
            synchronized (queue){
                int count=queue.size();
                if (count<=0){
                    return user?getPlaying():null;
                }
                nextIndex=indexer.next(mode,current,count,user);
                return nextIndex>=0&&nextIndex<count?queue.get(nextIndex):null;
            }
        }
        return null;
    }

    @Override
    public long getDuration() {
        IPlayable playable=getPlaying();
        return null!=playable&&playable instanceof NasMedia?((NasMedia)playable).getDuration():0;
    }

    @Override
    public float getCurrentProgress() {
        MediaBuffer buffer=getPlayingBuffer();
        if (null!=buffer&&buffer instanceof NetMediaBuffer){
            Long length=buffer.getContentLength();
            long next=null!=length&&length>0?((NetMediaBuffer)buffer).getCurrentPosition():-1;
            return next>0?(float)(next/(double)length):0;
        }
        return 0;
    }

    public final boolean pause(boolean stop, Object... objects) {
        IPlayable playing=super.getPlaying();
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

    public final boolean isExist(Object ...objs){
        return null!=indexInQueue(objs);
    }

    public final int index(Object media){
        Object[] objects= indexInQueue(media);
        Object object=null!=objects&&objects.length==2?objects[1]:null;
        return null!=object&&object instanceof Integer?(Integer) object:-1;
    }

    public final IPlayable indexMedia(Object media){
        return null!=media?findMedia(mQueue,media):null;
    }

    private final IPlayable findMedia(List<IPlayable> queue, Object media){
        Object[] objects= find(queue,media);
        Object object=null!=objects&&objects.length==2?objects[0]:null;
        return null!=object&&object instanceof IPlayable ?(IPlayable)object:null;
    }

    private Object[] indexInQueue(Object ...objects){
        List<IPlayable> list=mQueue;
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

    private Object[] find(List<IPlayable> queue, Object media){
        if (null!=media&&null!=queue){
            synchronized (queue){
                int size=null!=media&&null!=queue&&null!=media?queue.size():-1;
                IPlayable child;
                for (int i=0;i<size;i++){
                    child=queue.get(i);
                    if (null!=child&&child.equals(media)){
                        return new Object[]{media,i};
                    }
                }
            }
        }
        return null;
    }

    public final List<IPlayable> getQueue() {
        List<IPlayable> result=null;
        IPlayable playing=getPlaying();
        List<IPlayable> list=mQueue;
        int size=null!=list?list.size():0;
        final boolean existPlaying=null!=playing;
        if (size>0||existPlaying){
            result=new ArrayList<>(size+(existPlaying?1:0));
            result.addAll(list);
            if (existPlaying&&!result.contains(playing)){
                result.add(playing);
            }
        }
        return result;
    }

}
