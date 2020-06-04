package com.merlin.media;

public final class Playing<T extends IPlayable>  implements IPlayable {
    public final static int PLAYING_TYPE_NONE= 0x00;//0000 0000
    public final static int PLAYING_TYPE_PENDING= 0x01;//0000 0001
    public final static int PLAYING_TYPE_IN_QUEUE= 0x02;//0000 0010
    private T mPlaying;
    private int mType;

    public T getPlaying() {
        return mPlaying;
    }

    public int getType() {
        return mType;
    }


    @Override
    public String getTitle() {
        T playing=mPlaying;
        return null!=playing?playing.getTitle():null;
    }
}
