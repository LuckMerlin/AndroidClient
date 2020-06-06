package com.merlin.player;



import com.merlin.debug.Debug;
import com.merlin.id3.ID3;

final class Playing {
    private long mCursor;
    private final Playable mMedia;
    private Long mDuration;
    private Double mSeek;
    private byte mChannels;
    private int mSampleRate;
    private boolean mCacheOver=false;
    private Object mId3;

    Playing(Playable media,Double seek){
        mMedia=media;
        mSeek=seek;
        mCursor=0;
    }

    public ID3 onWriteId3(byte[] buffer, int offset, int size){//Try read ID3 data here
        Object id3=mId3;
        if ((null!=id3&&!(id3 instanceof ID3Reader))||(null==buffer||offset<0||size<=0||buffer.length<offset+size)){
            return null;
        }
        id3=mId3=(null!=id3&&id3 instanceof ID3Reader?(ID3Reader)id3:new ID3Reader()).onWrite(buffer,offset,size);
        return null!=id3&&id3 instanceof ID3?(ID3)id3:null;
    }

    public ID3 getID3() {
        Object object=mId3;
        return null!=object&&object instanceof ID3?((ID3)object):null;
    }

    public boolean setCacheOver(boolean over){
        mCacheOver=over;
        return false;
    }

    public void setChannels(byte channels) {
        this.mChannels = channels;
    }

    public boolean isMediaEquals(Object obj){
        Playable media=mMedia;
        return null!=obj&&null!=media&&media.equals(obj);
    }

    private long lengthToTime(long length){
        int sampleRate=length>0?mSampleRate:0;
        //        mp3 每帧均为1152个字节， 则：
//        frame_duration = 1152 * 1000000 / sample_rate
//        sample_rate = 44100HZ时， 计算出的时长为26.122ms，这就是经常听到的mp3每帧播放时间固定为26ms的由来。
//        Debug.D(getClass(),"AAAAAAAAa ");
        return sampleRate<=0?0:(length * 1000 / sampleRate);
    }

    public long getDuration() {
        Long duration=mDuration;
        if (null!=duration){
            return duration;
        }
        Playable playable=mMedia;
        Meta meta=null!=playable?playable.getMeta():null;
        long length=null!=meta?meta.getLength():-1;
        long time=length>0?lengthToTime(length):0;
        return mDuration=(time>0?time:null);
    }

    public long getPosition() {
        long loadCursor=mCursor;
        if(loadCursor<=0){
            return 0;
        }
        return lengthToTime(loadCursor);
    }

    public byte getChannels() {
        return mChannels;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    public boolean isCacheOver() {
        return mCacheOver;
    }

    public boolean setSeek(Double seek, String debug) {
        if (null==seek||seek>=-1){
            mSeek=seek;
            return true;
        }
        return true;
    }

    public long getCursor(){
        Double seek=mSeek;
        if (null!=seek) {
            Playable media = mMedia;
            Meta meta = null != media ? media.getMeta() : null;
            long length = null != meta ? meta.getLength() : -1;
            if (length>=0){
                long seekTo = resolveSeekPosition(mSeek, length, null);
                mSeek=null;
                if (seekTo >= 0) {
                    Debug.D(getClass(), "Seeking to " + seekTo);
                    mCursor=seekTo;
                }
            }
        }
        return mCursor;
    }

    public Playable getMedia() {
        return mMedia;
    }

    public long increaseCursor(int count){
        if (count>0){
            mCursor+=count;
        }
        return mCursor;
    }

    private long resolveSeekPosition(double seek, long length, String debug){
        if (length<=0){
            Debug.W(getClass(),"Can't resolve seek position while length invalid "+(null!=debug?debug:"."));
            return -1;
        }
        return (long)((seek=seek<-1?0:seek)<0?length*-seek:seek);
    }

    public void setSampleRate(int sampleRate) {
        mSampleRate=sampleRate;
    }
}
