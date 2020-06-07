package com.merlin.player;

public class Meta {
    private long mLength;
    private String mName;
    private String mTitle;
    private final long mDuration=0;
    private final String mArtist="林强";
    private final String mAlbum="厉害";
    private String mMd5;
    private String mHost;

    public Meta(long length,String name,String title,String md5){
        mLength=length;
        mName=name;
        mTitle=title;
        mMd5=md5;
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

    public String getMd5(){
        return mMd5;
    }

    public String getHost() {
        return mHost;
    }
}
