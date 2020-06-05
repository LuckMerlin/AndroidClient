package com.merlin.player;

import com.merlin.debug.Debug;

final class Playing {
    private long mCursor;
    private final Playable mMedia;
    private Double mSeek;

    Playing(Playable media,Double seek){
        mMedia=media;
        mSeek=seek;
        mCursor=0;
    }

    public boolean setSeek(Double seek,String debug) {
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

}
