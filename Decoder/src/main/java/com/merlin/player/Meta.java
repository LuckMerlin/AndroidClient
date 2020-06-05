package com.merlin.player;

public final class Meta {
    private final long mLength;
    private final String mName;
    private final String mTitle;
    private final long mDuration=0;
    private final String mArtist="林强";
    private final String mAlbum="厉害";

    public Meta(long length,String name,String title){
        mLength=length;
        mName=name;
        mTitle=title;
    }

    public long getDuration() {
        return mDuration;
    }

    public long getLength() {
        return mLength;
    }

    public String getName(){
        return mName;
    }

    public String getTitle(){
        return mTitle;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }
}
